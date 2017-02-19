package localisation;

import java.util.ArrayList;

import rp.robotics.mapping.GridMap;

public class ProbableLocations {

	private GridMap map;
	private float coordRange;
	private float cellSize;
	private float beginningRange;
	private float appropriateRange;
	private float doubleCellRange;
	
	private final float cameraWheelsDistance = 2.0f;
	private final float zeroProbability = 0;
	private final float fullProbability = 1;
	private final float probabilitySmall = 0.20f;
	private final float probabilityBig = 0.80f;
	
	private ArrayList<PosProb> locs = new ArrayList<PosProb>();
	private ArrayList<PosProb> newLocs = new ArrayList<PosProb>();
	
	public ProbableLocations(GridMap map){
		this.map=map;
		this.cellSize = map.getCellSize()*100;
		this.beginningRange = cellSize;
		this.appropriateRange = 5*cellSize/3;
		this.doubleCellRange = 2*cellSize;
		

		for(int i=0;i<=map.getXSize();i++){
		
			for(int j=0;j<=map.getYSize();j++){
				if(!map.isObstructed(i, j)){
					locs.add(new PosProb(i, j, (float)1/initialSize()));				
				}
			}
		}
	}
	
	public void setLocations(float heading,float range){
		//range = range + cameraWheelsDistance;
		
		for(int p = 0; p < locs.size(); p++){
			int i = (int)locs.get(p).getxCoord();
			int j = (int)locs.get(p).getyCoord();
			coordRange=map.rangeToObstacleFromGridPosition(i, j, heading)*100;
				
				if(range <= beginningRange){
					if(coordRange <= beginningRange){
//						float newProbability = 1;
//						float oldProbability = locs.get(p).getProbability();
//						locs.get(p).setProbability(newProbability*oldProbability);
						newProbability(p, fullProbability);
						//System.out.println("*i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());
					}else{
//						float newProbability = 0;
//						float oldProbability = locs.get(p).getProbability();
//						locs.get(p).setProbability(newProbability*oldProbability);
						newProbability(p, zeroProbability);
						//System.out.println("**i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());

					}
				}else if(range > beginningRange && range <= doubleCellRange){
					if(coordRange <= beginningRange){
//						float newProbability = 0;
//						float oldProbability = locs.get(p).getProbability();
//						locs.get(p).setProbability(newProbability*oldProbability);
						newProbability(p, zeroProbability);
						//System.out.println("***i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());

						
					}else if(coordRange > beginningRange && coordRange <= appropriateRange){
//						float newProbability = 0.85f;
//						float oldProbability = locs.get(p).getProbability();
//						locs.get(p).setProbability(newProbability*oldProbability);
						newProbability(p, probabilityBig);
						//System.out.println("****i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());

					}else if(coordRange > appropriateRange){
//						float newProbability = 0.15f;
//						float oldProbability = locs.get(p).getProbability();
//						locs.get(p).setProbability(newProbability*oldProbability);
						newProbability(p, probabilitySmall);
						//System.out.println("*****i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());

					}
				}else if(range > doubleCellRange){
						if(coordRange < appropriateRange){
//							float newProbability = 0;
//							float oldProbability = locs.get(p).getProbability();
//							locs.get(p).setProbability(newProbability*oldProbability);
							newProbability(p, zeroProbability);
							//System.out.println("******i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());

						}else{
//							float newProbability = 1;
//							float oldProbability = locs.get(p).getProbability();
//							locs.get(p).setProbability(newProbability*oldProbability);
							newProbability(p, fullProbability);
							//System.out.println("*******i = " + i + "; j = " + j + " coordRange: " + coordRange + " prob: " + locs.get(p).getProbability());

						}
				}
			}
		
		removeInvalid();
		
		normalise();			
				
	}
	
	private void newArrayUpdate(){
		newLocs.clear();
		for(int q = 0; q < locs.size(); q++){
			newLocs.add(locs.get(q));
		}
	}
	
	private void removeInvalid(){
		
		newArrayUpdate();
		
		for(int q = 0; q < newLocs.size(); q++){
			int i = (int)newLocs.get(q).getxCoord();
			int j = (int)newLocs.get(q).getyCoord();
			
			if(map.isObstructed(i, j) || newLocs.get(q).getProbability() == zeroProbability){
				
				PosProb pos = newLocs.get(q);
				locs.remove(pos);

				//System.out.println("********i = " + i + "; j = " + j + " coordRange: " + coordRange );
				//System.out.println("locs size: " + locs.size() + " newLocs size: " + newLocs.size());
			}
		}
	}
	
	public float getCellSize(){
		return map.getCellSize()*100;
	}
	
	public PosProb getPoints(int i){
		return locs.get(i);
	}
	
	public int size(){
		return locs.size();
	}
	
	public int initialSize(){
		int count = 0;
		for(int i=0;i<=map.getXSize();i++){
			
			for(int j=0;j<=map.getYSize();j++){
				if(!map.isObstructed(i, j)){
					count++;
				
				}
			}
		}
		return count;
	}
	
	public int sizeNew(){
		return newLocs.size();
	}
	
	private void normalise(){
		float total = sumProbabilities();
		
		for(int i = 0; i < locs.size(); i++){
			if(!map.isObstructed(locs.get(i).getxCoord(), locs.get(i).getyCoord()) && locs.get(i).getProbability() != 0.0f){
				float prob = locs.get(i).getProbability();
				locs.get(i).setProbability(prob/total);
			}
		}
	}
	
	private float sumProbabilities(){
		float total = 0;
		for(int i = 0 ; i < locs.size(); i++){
			if(!map.isObstructed(locs.get(i).getxCoord(), locs.get(i).getyCoord()) && locs.get(i).getProbability() != 0.0f){
				total += locs.get(i).getProbability();
			}
		}
		
		return total;
	}
	
	private void newProbability(int p, float newProbability){
		float oldProbability = locs.get(p).getProbability();
		locs.get(p).setProbability(newProbability*oldProbability);
	}
	
	public void updateLocations(float heading){
		if(heading==180)
			for(int i=0;i<locs.size();i++){
				locs.set(i,new PosProb(locs.get(i).getxCoord()-1,locs.get(i).getyCoord(), locs.get(i).getProbability()));
				
			}
		else if(heading==0)
			for(int i=0;i<locs.size();i++){
				locs.set(i,new PosProb(locs.get(i).getxCoord() + 1,locs.get(i).getyCoord(), locs.get(i).getProbability()));
			}
		else if(heading==90){
			for(int i=0;i<locs.size();i++){
				locs.set(i,new PosProb(locs.get(i).getxCoord(),locs.get(i).getyCoord() + 1, locs.get(i).getProbability()));
			}
		}
		else if(heading==-90)
			for(int i=0;i<locs.size();i++){
				locs.set(i,new PosProb(locs.get(i).getxCoord(),locs.get(i).getyCoord() - 1, locs.get(i).getProbability()));
			}
		else 
			assert false : "Unknown value for enumeration";
		
		removeInvalid();
	}
}
