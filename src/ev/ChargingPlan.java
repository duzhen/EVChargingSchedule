package ev;

public class ChargingPlan {

	public EV ev;
	public Charging charging;
	public ChargingPlan(EV ev, Charging charging) {
		this.ev = ev;
		this.charging = charging;
	}

}
