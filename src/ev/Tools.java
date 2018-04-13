package ev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
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
		IntervalCategoryDataset dataset = createDataset(plans);
		JFreeChart chart = ChartFactory.createGanttChart("EV Charging Schedule", "EV", "Charging time", dataset, false,
				false, false);
		CategoryPlot plot = chart.getCategoryPlot();
		DateAxis da = (DateAxis) plot.getRangeAxis(0);
		da.setDateFormatOverride(new SimpleDateFormat("HH:mm"));

		FileOutputStream fop = null;
		try {
			fop = new FileOutputStream(file);
			ChartUtilities.writeChartAsJPEG(fop, 1f, chart, 1000, 2000, null);
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
////			int minutes = Float.valueOf((plan.ev.start - Float.valueOf(plan.ev.start).intValue()) * 60).intValue();
////			Calendar calendar = Calendar.getInstance();
////			calendar.set(Calendar.HOUR_OF_DAY, Float.valueOf(plan.ev.start).intValue());
////			calendar.set(Calendar.MINUTE, minutes);
////			System.out.println(calendar.getTime());
//			Date d1 = calendar.getTime();
//			Date d2 = new Date((d1.getTime() + plan.ev.chargingDuration * 60 * 1000));
			Task t1 = new Task(String.valueOf(plan.ev.id), plan.ev.getStartDate(), plan.ev.getChargingFinishDate());
//			t1.setDescription(plan.charging.id+"");
			s1.add(t1);
		}
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);
		return collection;
	}
}
