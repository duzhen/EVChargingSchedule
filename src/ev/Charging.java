package ev;

import java.util.Arrays;

public class Charging {
	public int id;
	public int type;
	public float freeTime;
	public float[] chargingSpeed = new float[3];
	public Charging(int id, int type) {
		this.id = id;
		this.type = type;
		if(type == 1) {
			chargingSpeed[0] = 0.37f;
			chargingSpeed[1] = 0.40f;
			chargingSpeed[2] = 0.42f;
		} else if(type == 2) {
			chargingSpeed[0] = 1.20f;
			chargingSpeed[1] = 0f;
			chargingSpeed[2] = 1.42f;
		} else if(type == 3) {
			chargingSpeed[0] = 0f;
			chargingSpeed[1] = 2.20f;
			chargingSpeed[2] = 0f;
		} else if(type == 4) {
			chargingSpeed[0] = 0f;
			chargingSpeed[1] = 0f;
			chargingSpeed[2] = 2.67f;
		}
	}
	@Override
	public String toString() {
		return "Charging [id=" + id + ", type=" + type + ", freeTime=" + freeTime + ", chargingSpeed="
				+ Arrays.toString(chargingSpeed) + "]";
	}
	
}
