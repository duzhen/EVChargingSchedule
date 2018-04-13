package ev;

import java.util.ArrayList;
import java.util.Comparator;

public class EVCharging {
	private ArrayList<EV> evs;
	private ArrayList<EV> evs2;//SortByFinishTime
	private ArrayList<Charging> chargings;
	private ArrayList<ChargingPlan> chargingPlan;
	public EVCharging(String file) {
		chargingPlan = new ArrayList<ChargingPlan>();
		evs = Tools.parseEV(file);
		chargings = Tools.parseCharging(file);
		evs2 = new ArrayList<EV>();
		for(EV e : evs) {
			evs2.add(e.clone());
		}
		evs.sort(new Comparator<EV>( ) {

			@Override
			public int compare(EV arg0, EV arg1) {
//				return (int)(10*(arg0.start - arg1.start));
				if(arg0.start - arg1.start != 0) {
					return (int)(10*(arg0.start - arg1.start));
				} else {
//					return arg1.type-arg0.type;
//					return arg1.mile-arg0.mile;
//					return (int)((arg1.finish-arg1.start-arg1.mile/arg1.type)
//							-(arg0.finish-arg0.start-arg0.mile/arg0.type));
					return (int)((arg1.finish-arg1.start-arg1.mile/arg1.type)
							-(arg0.finish-arg0.start-arg0.mile/arg0.type));
				}
			}
			
		});
//		evs.sort(new Comparator<EV>( ) {
//
//			@Override
//			public int compare(EV arg0, EV arg1) {
//				if(arg0.finish - arg1.finish != 0) {
//					return (int)(10*(arg1.finish - arg0.finish));
//				} else {
//					return arg0.mile-arg1.mile;
//				}
//			}
//			
//		});
//		evs2.sort(new Comparator<EV>( ) {
//
//			@Override
//			public int compare(EV arg0, EV arg1) {
//				return (int)(10*(arg0.finish - arg1.finish));
//			}
//			
//		});
		for(EV v : evs) {
			System.out.println(v.toString());
		}
		for(Charging c: chargings) {
			c.freeTime = evs.get(0).start;
			System.out.println(c.toString());
		}
	}
	
	private void start(String file) {
		greedy(0, evs, chargings, chargingPlan);
		System.out.println("We can charge " + chargingPlan.size() + " EVs");
		chargingPlan.sort(new Comparator<ChargingPlan>( ) {

			@Override
			public int compare(ChargingPlan arg0, ChargingPlan arg1) {
				return arg0.ev.id - arg1.ev.id;
			}
			
		});
		Tools.writeResults(file, chargingPlan);
		Tools.drawGanttChart("gantt.jpg", chargingPlan);
//		for(int i=0;i<evs.size()-1;i++) {
//			ArrayList<EV> ev1 = new ArrayList<EV>(evs.subList(0, i));
//			ArrayList<EV> ev2 = new ArrayList<EV>(evs.subList(i, evs.size()-1));
////			System.out.println(ev1.size());
////			System.out.println(ev2.size());
//			greedy(0, ev1, chargings, chargingPlan);
//			greedy(0, ev2, chargings, chargingPlan);
//			System.out.println("We can charge " + chargingPlan.size() + " EVs");
//			chargingPlan.clear();
//			for(Charging c : chargings) {
//				c.freeTime = 0;
//			}
//			break;
//		}
	}
	
//	private int beforeFinishTimeFromEV2(float finish) {
//		for(int i = evs2.size()-1;i>=0;i--) {
//			if(evs2.get(i).finish < finish) {
//				return i;
//			}
//		}
//		return -1;
//	}
//	
//	private int ifBetter(float start, float finish, int index, ArrayList<EV> evs, ArrayList<Charging> chargings) {
//		//charging it
//		for(EV e : evs.subList(index, evs.size()-1)) {
//			int bestChargingId = -1;
//			for(Charging c : chargings) {
//				
//			}
//		}
//		//no charging
//	}
	
	private void greedy2(int index, ArrayList<EV> evs, ArrayList<Charging> chargings, ArrayList<ChargingPlan> chargingPlan) {
		if(index >= evs.size()) {
			return;
		}
		EV ev = evs.get(index);
		float max = Float.MIN_VALUE;
		Charging charging = null;
		for(Charging c : chargings) {
			if(c.chargingSpeed[ev.type-1] > 0 && c.chargingSpeed[ev.type-1] > max 
					&& c.freeTime < ev.finish - (ev.mile/c.chargingSpeed[ev.type-1])/60
					&& ev.finish - (ev.mile/c.chargingSpeed[ev.type-1])/60 >= ev.start) {
				charging = c;
			}
		}
		if(charging != null) {
			charging.freeTime = ev.finish;//ev.start + (ev.mile/charging.chargingSpeed[ev.type-1])/60;
			ev.chargingDuration = (int)Math.floor(ev.mile/charging.chargingSpeed[ev.type-1]);
			chargingPlan.add(new ChargingPlan(ev, charging));
			System.out.println("Find an Charging Port:" + charging.id + ", start:"+ev.start + " charging Duration:" + ev.chargingDuration + "minutes");
		}
		greedy(index+1, evs, chargings, chargingPlan);
	}
	private void greedy(int index, ArrayList<EV> evs, ArrayList<Charging> chargings, ArrayList<ChargingPlan> chargingPlan) {
		if(index >= evs.size()) {
			return; 
		}
		EV ev = evs.get(index);
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		int method = 0;
		Charging charging = null;
		for(Charging c : chargings) {
			if(c.chargingSpeed[ev.type-1] > 0) {// && c.chargingSpeed[ev.type-1] > max) {
				if(c.freeTime < ev.start
						&& ev.start + (ev.mile/c.chargingSpeed[ev.type-1])/60 < ev.finish) {
					if((ev.mile/c.chargingSpeed[ev.type-1])/60 < min) {
						charging = c;
						method = 1;
						min = (ev.mile/c.chargingSpeed[ev.type-1])/60;
//						break;
					}
				} else if(c.freeTime + (ev.mile/c.chargingSpeed[ev.type-1])/60 < ev.finish) {
					if((ev.mile/c.chargingSpeed[ev.type-1])/60 < min) {
						charging = c;
						method = 2;
						min = (ev.mile/c.chargingSpeed[ev.type-1])/60;
//						break;
					}
				}
			}
		}
		if(charging != null) {
			if(method == 1) {
				charging.freeTime = ev.start + (ev.mile/charging.chargingSpeed[ev.type-1])/60;
			} else if(method == 2) {
				ev.start = charging.freeTime;
				charging.freeTime = charging.freeTime + (ev.mile/charging.chargingSpeed[ev.type-1])/60;
			}
			ev.chargingDuration = (int)Math.ceil(ev.mile/charging.chargingSpeed[ev.type-1]);
			chargingPlan.add(new ChargingPlan(ev, charging));
			System.out.println("EV " + ev.id + " find an Charging Port:" + charging.id + ", start:"+ev.start + " charging Duration:" + ev.chargingDuration + "minutes");
		}
		greedy(index+1, evs, chargings, chargingPlan);
//		if(index+2 < evs.size() && evs.get(index+2).start !=ev.start) {
//			greedy(index+2, evs, chargings, chargingPlan);
//		} else {
//			greedy(index+1, evs, chargings, chargingPlan);
//		}
	}
	
	public static void main(String[] args) {
		String file = "res/test data3.xlsx";
		EVCharging ec = new EVCharging(file);
		ec.start(file);
	}

}
