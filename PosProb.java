package localisation;

public class PosProb {
	
	private int xCoord;
	private int yCoord;
	private float probability;
	
	public PosProb(int xCoord, int yCoord, float probability){
		this.setxCoord(xCoord);
		this.setyCoord(yCoord);
		this.setProbability(probability);
	}

	public int getxCoord() {
		return xCoord;
	}

	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	public float getProbability() {
		return probability;
	}

	public void setProbability(float probability) {
		this.probability = probability;
	}

}
