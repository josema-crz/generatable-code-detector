package utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class HistogramCalculator {
	static class Interval {
		int min, max;
		String label;
		int count;

		public Interval(int min, int max, String label) {
			super();
			this.min = min;
			this.max = max;
			this.label = label;
			count = 0;
		}

		public int getMin() {
			return min;
		}

		public int getMax() {
			return max;
		}

		public String getLabel() {
			return label;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}

	public static void main(String[] args) {
		List<Interval> intervals = new LinkedList<Interval>();		
		intervals.add(new Interval(0, 0, "0"));
		intervals.add(new Interval(1, 30, "1-30"));
		intervals.add(new Interval(31, 100, "31-100"));
		intervals.add(new Interval(101, 200, "101-200"));
		intervals.add(new Interval(201, 500, "201-500"));
		intervals.add(new Interval(501, 1000, "501-1000"));
		intervals.add(new Interval(1001, 1500, "1001-1500"));
		intervals.add(new Interval(1501, 2000, "1501-2000"));

		Path filePath = Paths.get(
				"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\04.Ausarbeitung\\src\\casestudy-results\\raw\\Name-and-code-similarity\\__nogenGroupsSims.txt");
		Scanner scanner;
		try {
			scanner = new Scanner(filePath);
			scanner.useLocale(Locale.US);

			while (scanner.hasNext()) {
				if (scanner.hasNextDouble()) {
					double number = scanner.nextDouble();

					for (Interval interval : intervals) {
						if (number >= interval.getMin() && number <= interval.getMax()) {
							interval.setCount(interval.getCount() + 1);
							break;
						}
					}

				} else {
					scanner.next();
				}
			}

			for (Interval interval : intervals) {
				System.out.println(interval.getLabel() + " ->" + interval.getCount());
			}
			
			scanner.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
