package ev;

import java.util.Calendar;
import java.util.Date;

public class EV {
	public int id;
	public float start;
	public float finish;
	public int mile;
	public int type;
	public int chargingDuration;

	public EV(int id, float start, float finish, int mile, int type) {
		this.id = id;
		this.start = start;
		this.finish = finish;
		this.mile = mile;
		this.type = type;
	}

	public Date getStartDate() {
		int minutes = Float.valueOf((this.start - Float.valueOf(this.start).intValue()) * 60).intValue();
		String m = "";
		if (minutes < 10) {
			m = "0" + minutes;
		} else {
			m = String.valueOf(minutes);
		}
		// cell.setCellValue(Float.valueOf(plan.ev.start).intValue() + ":"
		// + m);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.set(Calendar.HOUR_OF_DAY, Float.valueOf(start).intValue());
		calendar.set(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}
	
	public Date getChargingFinishDate() {
		return new Date((getStartDate().getTime() + chargingDuration * 60 * 1000));
	}
	
	@Override
	protected EV clone() {
		try {
			return (EV) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return "EV [id=" + id + ", start=" + start + ", finish=" + finish + ", mile=" + mile + ", type=" + type + "]";
	}
	
}
