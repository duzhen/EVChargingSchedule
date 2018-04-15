package ev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

public class Tools {

	public Tools() {
	}

	public static ArrayList<EV> parseEV(String file) {
		ArrayList<EV> evs = new ArrayList<EV>();
		try {
			File excel = new File(file);
			FileInputStream fis = new FileInputStream(excel);
			XSSFWorkbook book = new XSSFWorkbook(fis);
			XSSFSheet sheet = book.getSheetAt(0);
			Iterator<Row> itr = sheet.iterator();
			// Iterating over Excel file in Java
			if (itr.hasNext()) {
				itr.next();
			}
			while (itr.hasNext()) {
				Row row = itr.next();
				// Iterating over each column of Excel file
				int cellColumn = 0;
				String[] columns = new String[5];
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext() && cellColumn < 5) {
					Cell cell = cellIterator.next();
					if (DateUtil.isCellDateFormatted(cell)) {
						columns[cellColumn++] = String.valueOf(
								cell.getDateCellValue().getHours() + cell.getDateCellValue().getMinutes() / 60f);
					} else {
						columns[cellColumn++] = String.valueOf(cell.getNumericCellValue());
					}
				}
				if (cellColumn == 5) {
					evs.add(new EV(Float.valueOf(columns[0]).intValue(), Float.valueOf(columns[1]),
							Float.valueOf(columns[2]), Float.valueOf(columns[3]).intValue(),
							Float.valueOf(columns[4]).intValue()));
				}
			}
			book.close();
			fis.close();
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return evs;
	}

	public static ArrayList<Charging> parseCharging(String file) {
		ArrayList<Charging> chargings = new ArrayList<Charging>();
		try {
			File excel = new File(file);
			FileInputStream fis = new FileInputStream(excel);
			XSSFWorkbook book = new XSSFWorkbook(fis);
			XSSFSheet sheet = book.getSheetAt(1);
			Iterator<Row> itr = sheet.iterator();
			// Iterating over Excel file in Java
			if (itr.hasNext()) {
				itr.next();
			}
			while (itr.hasNext()) {
				Row row = itr.next();
				// Iterating over each column of Excel file
				int cellColumn = 0;
				String[] columns = new String[2];
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext() && cellColumn < 2) {
					Cell cell = cellIterator.next();
					if (DateUtil.isCellDateFormatted(cell)) {
						columns[cellColumn++] = String.valueOf(
								cell.getDateCellValue().getHours() + cell.getDateCellValue().getMinutes() / 60f);
					} else {
						columns[cellColumn++] = String.valueOf(cell.getNumericCellValue());
					}
				}
				if (cellColumn == 2) {
					chargings.add(
							new Charging(Float.valueOf(columns[0]).intValue(), Float.valueOf(columns[1]).intValue()));
				}
			}
			book.close();
			fis.close();
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return chargings;
	}

	public static void writeResults(String file, ArrayList<ChargingPlan> plans) {
		try {
			File excel = new File(file);
			FileInputStream fis = new FileInputStream(excel);
			XSSFWorkbook book = new XSSFWorkbook(fis);
			book.createSheet("Output");
			XSSFSheet sheet = book.getSheetAt(2);
			int rownum = 0;
			int cellnum = 0;
			Row row = sheet.createRow(rownum++);
			Cell cell = row.createCell(cellnum++);
			cell.setCellValue("Cusomer Id");
			cell = row.createCell(cellnum++);
			cell.setCellValue("EV type");
			cell = row.createCell(cellnum++);
			cell.setCellValue("Start Charging Time");
			cell = row.createCell(cellnum++);
			cell.setCellValue("Charging Duration");
			cell = row.createCell(cellnum++);
			cell.setCellValue("Assigned Charging Point");
			for (ChargingPlan plan : plans) {
				row = sheet.createRow(rownum++);
				cellnum = 0;
				cell = row.createCell(cellnum++);
				cell.setCellValue(plan.ev.id);
				cell = row.createCell(cellnum++);
				cell.setCellValue(plan.ev.type);
				cell = row.createCell(cellnum++);
				
//				int minutes = Float.valueOf((plan.ev.start - Float.valueOf(plan.ev.start).intValue()) * 60).intValue();
//				String m = "";
//				if (minutes < 10) {
//					m = "0" + minutes;
//				} else {
//					m = String.valueOf(minutes);
//				}
//				// cell.setCellValue(Float.valueOf(plan.ev.start).intValue() + ":"
//				// + m);
//
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTimeInMillis(0);
//				calendar.set(Calendar.HOUR_OF_DAY, Float.valueOf(plan.ev.start).intValue());
//				calendar.set(Calendar.MINUTE, minutes);
//				System.out.println(calendar.getTime());

				CellStyle cellStyle = book.createCellStyle();
				CreationHelper createHelper = book.getCreationHelper();
				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("hh:mm"));
				cell.setCellValue(plan.ev.getStartDate());

				cell = row.createCell(cellnum++);
				cell.setCellValue(plan.ev.chargingDuration + "min");
				cell = row.createCell(cellnum++);
				cell.setCellValue(plan.charging.id);
			}
			row = sheet.createRow(rownum + 1);
			cell = row.createCell(0);
			cell.setCellValue("The number of EV:" + plans.size());
			// open an OutputStream to save written data into Excel file
			FileOutputStream os = new FileOutputStream(excel);
			book.write(os);
			System.out.println("Writing on Excel file Finished ...");
			// Close workbook, OutputStream and Excel file to prevent leak
			os.close();
			book.close();
			fis.close();
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public static void drawGanttChart(String file, ArrayList<ChargingPlan> plans) {
		ArrayList<Integer> evs = new ArrayList<Integer>();
		ArrayList<Integer> chargings = new ArrayList<Integer>();
		for (ChargingPlan plan : plans) {
			if(!evs.contains(plan.ev.id)) {
				evs.add(plan.ev.id);
			}
			if(!chargings.contains(plan.charging.id)) {
				chargings.add(plan.charging.id);
			}
		}
		IntervalCategoryDataset dataset = createDataset(plans);
		JFreeChart chart = ChartFactory.createGanttChart("EV Charging Schedule", "EV", "Charging time", dataset, false,
				false, false);
		CategoryPlot plot = chart.getCategoryPlot();
		DateAxis da = (DateAxis) plot.getRangeAxis(0);
		da.setDateFormatOverride(new SimpleDateFormat("HH:mm"));

		FileOutputStream fop = null;
		try {
			fop = new FileOutputStream(file);
			ChartUtilities.writeChartAsJPEG(fop, 1f, chart, 200*chargings.size(), 50*evs.size(), null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fop.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static IntervalCategoryDataset createDataset(ArrayList<ChargingPlan> plans) {
		final TaskSeries s1 = new TaskSeries("SCHEDULE");
		for (ChargingPlan plan : plans) {
			Task t1 = new Task(String.valueOf(plan.ev.id), plan.ev.getStartDate(), plan.ev.getChargingFinishDate());
//			t1.setPercentComplete(0.1*plan.charging.id);
			s1.add(t1);
		}
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);
		return collection;
	}

	public static void drawGanttChart2(String file, ArrayList<ChargingPlan> plans) {
		ArrayList<Integer> evs = new ArrayList<Integer>();
		ArrayList<Integer> chargings = new ArrayList<Integer>();
		for (ChargingPlan plan : plans) {
			if(!evs.contains(plan.ev.id)) {
				evs.add(plan.ev.id);
			}
			if(!chargings.contains(plan.charging.id)) {
				chargings.add(plan.charging.id);
			}
		}
		IntervalCategoryDataset dataset = createDataset2(plans);
		JFreeChart chart = ChartFactory.createGanttChart("EV Charging Schedule", "Charging Port", "Charging time", dataset, false,
				false, false);
		CategoryPlot plot = chart.getCategoryPlot();
		DateAxis da = (DateAxis) plot.getRangeAxis(0);
		da.setDateFormatOverride(new SimpleDateFormat("HH:mm"));

		FileOutputStream fop = null;
		try {
			fop = new FileOutputStream(file);
			ChartUtilities.writeChartAsJPEG(fop, 1f, chart, 50*evs.size(), 100*chargings.size(), null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fop.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static ChargingPlan getLastChargingOnThisPort(int id, ArrayList<ChargingPlan> plans) {
		ChargingPlan p = null;
		for (ChargingPlan plan : plans) {
			if(plan.charging.id==id) {
				p = plan;
			} else {
				if(p!=null)
					break;
			}
		}
		return p;
	}
	private static IntervalCategoryDataset createDataset2(ArrayList<ChargingPlan> plans) {
		plans.sort(new Comparator<ChargingPlan>( ) {

			@Override
			public int compare(ChargingPlan arg0, ChargingPlan arg1) {
				if(arg0.charging.id - arg1.charging.id != 0) {
					return arg0.charging.id - arg1.charging.id;
				}
				return (int)((arg0.ev.start - arg1.ev.start)*100);
			}
			
		});
		final TaskSeries s1 = new TaskSeries("SCHEDULE");
		HashMap<Integer, Task> charging = new HashMap<Integer, Task>();
		for (ChargingPlan plan : plans) {
			if(!charging.containsKey(plan.charging.id)) {
				ChargingPlan p = getLastChargingOnThisPort(plan.charging.id, plans);
				Task sub = new Task(String.valueOf(plan.charging.id), plan.ev.getStartDate(), plan.ev.getChargingFinishDate());
				Task t1 = new Task(String.valueOf(plan.charging.id), plan.ev.getStartDate(), p.ev.getChargingFinishDate());
				sub.setPercentComplete(0.1);
				t1.addSubtask(sub);
				charging.put(plan.charging.id, t1);
			} else {
				Task t1 = new Task(String.valueOf(plan.charging.id), plan.ev.getStartDate(), plan.ev.getChargingFinishDate());
				t1.setPercentComplete(0.1);
				charging.get(plan.charging.id).addSubtask(t1);
			}
		}
		for(Task t: charging.values()) {
			s1.add(t);
		}
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);
		return collection;
	}
}
