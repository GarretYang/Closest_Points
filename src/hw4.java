import java.io.*;
import java.util.HashMap;
import java.util.*;


public class hw4 {
	// a class that represent the edges of the graph
	private static Scanner input;
	
	public static class Point {
		private double x;
		private double y;
		
		Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public double getX() {
			return this.x;
		}
		
		public double getY() {
			return this.y;
		}		
		
		public void printPoint() {
			System.out.print("(" + this.x + ", " + this.y + ")");
		}
		
		// compare method for hasmap
		public boolean equals(Object obj) {
			if (!(obj instanceof Point))
				return false;	
			if (obj == this)
				return true;
			return (this.x == (((Point) obj).x)) && (this.y == (((Point) obj).y)) ;
		}		

		// hashcode for the object
        public int hashCode() {
            int result =  (int) Math.floor(this.x * 1000000) / 1000000 ;
            result = 37 * result + (int) Math.floor(this.y * 1000000) / 1000000 ;
            return result;
        }		
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException{	
        if(args.length == 0) {
        	System.out.println("Incorrect File Input");
        	System.exit(1);
        } 
        
//        String outputFile = "F:\\ebook\\CSE417\\HW\\HW4\\hw4-testcase\\300.txt";
//        randomPointGenerator(300, false, "", outputFile);
       
     // Take inputs from the commanline
        for (String fileName: args) {
        	File file = new File(fileName);
			input = new Scanner(file);
			
	        // a graph that records or vertexes and their corresponding edges
			
			ArrayList<Point> coordinateList = createList(input);      
	        
			printResult("Version 1", coordinateList, "");       
        }
	}
	
	// brute force calculates all pairwise distances
	private static double version1(List<Point> list, List<Point> pair) {
		int length = list.size();
		if (length == 1) return Double.POSITIVE_INFINITY;		
		
		// Initialize the pair list
		if (pair.size() == 0) {
			pair.add(list.get(0));
			pair.add(list.get(1));
		}			
		
		double min = calDistance(list.get(0),list.get(1));		
		for (int i = 0; i < length; i++) {
			for (int j = i + 1; j < length; j++) {
				double res = calDistance(list.get(i),list.get(j));
				if (res < min) {
					min = res;
					pair.set(0, list.get(i));
					pair.set(1, list.get(j));
				}
			}
		}
		return min;
	}
	
	// divide-and-conquer O(n * logn * logn)
	// sort by y-coordinates in every recursive call
	private static double version2(List<Point> list, List<Point> pair) {
		if (list.size() <= 1) return Double.POSITIVE_INFINITY;
		
		// Initialize the pair list
		if (pair.size() == 0) {
			pair.add(list.get(0));
			pair.add(list.get(1));
		}		
		double pairDistance = calDistance(pair.get(0),pair.get(1));
		
		int length = list.size();
		
		// The x coordinate of the mid line
		double midLine = list.get(length/2).getX();
		
		List<Point> left = new ArrayList<>(list.subList(0, length/2));
		List<Point> right = new ArrayList<>(list.subList(length/2, length));
		
		double min1 = version2(left, pair);
		double min2 = version2(right, pair);
		double min = Math.min(min1, min2);
		
		List<Point> midLane = new ArrayList<Point>();
		double leftBound = midLine - min;
		double rightBound = midLine + min;
		 
		for (Point item:list) {
			if (leftBound <= item.getX() && item.getX() <= rightBound) {
				midLane.add(item);
			}
		}
		
        // sort the coordinates by y
        midLane.sort(Comparator.comparing(Point::getY));		
		
		for (int i = 0; i < midLane.size(); i++) {
			int k = 1;
			while (i + k < midLane.size() && midLane.get(i + k).getY() < midLane.get(i).getY() + min) {
				double distance = calDistance(midLane.get(i), midLane.get(i + k));
				min = Math.min(min,distance);
				if (min < pairDistance) {
					pair.set(0, midLane.get(i));
					pair.set(1, midLane.get(i + k));
					pairDistance = min;
				}				
				k++;
			}
		}
		return min;
	}

	// divide-and-conquer O(n * logn)
	// Pre-sort by y-coordinates
	private static double version3(List<Point> xList, List<Point> yList, List<Point> pair) {
		if (xList.size() <= 1) return Double.POSITIVE_INFINITY;
		
		// Initialize the pair list
		if (pair.size() == 0) {
			pair.add(xList.get(0));
			pair.add(xList.get(1));
		}
		
		double pairDistance = calDistance(pair.get(0),pair.get(1));
		
		int length = xList.size();
		
		// The x coordinate of the mid line
		double midLine = xList.get(length/2).getX();
		
		List<Point> xLeft = new ArrayList<>(xList.subList(0, length/2));
		List<Point> xRight = new ArrayList<>(xList.subList(length/2, length));
		
		List<Point> yLeft = new ArrayList<>();
		List<Point> yRight = new ArrayList<>();
		
		for (Point item : yList) {
			if (item.getX() <= midLine) {
				yLeft.add(item);
			} else {
				yRight.add(item);
			}
		}		
		
		double min1 = version3(xLeft,yLeft,pair);
		double min2 = version3(xRight,yRight,pair);
		double min = Math.min(min1, min2);
		
		ArrayList<Point> midLane = new ArrayList<>();
		double leftBound = midLine - min;
		double rightBound = midLine + min;
		
		for (Point item: yList) {
			if (leftBound <= item.getX() && item.getX() <= rightBound) {
				midLane.add(item);
			}
		}			
		
		for (int i = 0; i < midLane.size(); i++) {
			int k = 1;
			while (i + k < midLane.size() && midLane.get(i + k).getY() < midLane.get(i).getY() + min) {
				double distance = calDistance(midLane.get(i), midLane.get(i + k));				
				min = Math.min(min,distance);				
				if (min < pairDistance) {
					pair.set(0, midLane.get(i));
					pair.set(1, midLane.get(i + k));
					pairDistance = min;
				}
				k++;
			}
		}
		return min;
	}
	
	// this method calculates the distance of two given points
	private static double calDistance(Point a, Point b) {
		double sqrX = (a.getX() - b.getX()) * (a.getX() - b.getX());
		double sqrY = (a.getY() - b.getY()) * (a.getY() - b.getY());		
		return Math.sqrt(sqrX + sqrY);
	}
 	// this method takes in txt input and generate a list of Point objects
	private static ArrayList<Point> createList(Scanner input) {
		
		ArrayList<Point> list = new ArrayList<Point>();
		
		while (input.hasNext()) {
        	double x = Double.parseDouble(input.next());
        	double y = Double.parseDouble(input.next());
      
        	Point coordinate = new Point(x,y);
        	list.add(coordinate);
 		}
		
		return list;
		
	}
	
	// This method generated random points that were either placed in squares
	// or in one vertical line. The points can have duplicates which depends on
	// the input boolean
	private static void randomPointGenerator(int size, boolean duplicates, String mode, String outputName) 
											throws FileNotFoundException, IOException{
		Random r= new Random();
		HashSet<Point> set = new HashSet<Point>();
        PrintStream out = new PrintStream(new File(outputName)); 
        PrintStream console = System.out;
        System.setOut(out);
        
		if (mode.equals("vertical")) {
			for (int i = 0; i < size; i++) {
				int y = r.nextInt(size); //Math.floor(r.nextDouble() * 1000000) / 1000000.0;
				Point p = new Point(0,y);
				
				if (!duplicates) {
					while (set.contains(p)) {
						y = r.nextInt(size);//Math.floor(r.nextDouble() * 1000000) / 1000000.0;
						p = new Point(0,y);
					}
				}
				set.add(p);
				System.out.print(0 + " ");
				System.out.print(y + " ");
			}
		} else {
			for (int i = 0; i < size; i++) {
				double x = r.nextInt(size);
				double y = r.nextInt(size);
				Point p = new Point(x,y);
				
				if (!duplicates) {
					while (set.contains(p)) {
						x = r.nextInt(size);
						y = r.nextInt(size);
						p = new Point(x,y);
					}
				}
				set.add(p);
				System.out.print(x + " ");
				System.out.print(y + " ");
			}			
		}
        System.setOut(console);	
        System.out.println("Done!");
	}
	
	// print the analytic information of the algorithm
	private static void printResult(String version, List<Point> coordinateList, String outputName) 
									throws FileNotFoundException, IOException {
        // sort the points by x
        ArrayList<Point> xList = new ArrayList<Point>(coordinateList);
        xList.sort(Comparator.comparing(Point::getX));
        
        // sort the points by y
        ArrayList<Point> yList = new ArrayList<Point>(coordinateList);
        yList.sort(Comparator.comparing(Point::getY));			

        PrintStream console = System.out;
        if (!outputName.equals("")) {
	        PrintStream out = new PrintStream(new File(outputName)); 
	        System.setOut(out);        
        }
        
		System.out.println("(a) " + version);
		
		System.out.println("(b) The number of point in the input, " + coordinateList.size() + ",");
		
		List<Point> pair = new ArrayList<Point>();
		
		double min = 0;
		long startTime = System.nanoTime();
		if (version.equals("Version 1")) {
			min = version1(coordinateList, pair);
		} else if (version.equals("Version 2")) {
			min = version2(xList, pair); 
		} else if (version.equals("Version 3")) {
			min = version3(xList,yList, pair); 
		}
		long endTime = System.nanoTime();
		
		if (pair.size() == 0) {
			System.out.println("(c) No pairs of points in the input file");
			System.out.println("(d) The return min value is " + min);
		} else {
			System.out.print("(c) The coordinates of the closest pair of points are ");
			pair.get(0).printPoint();
			System.out.print(", ");
			pair.get(1).printPoint();
			System.out.println();
			System.out.println("(d) The distance \"delta\" between them is " + min);
		}
		
		System.out.println("(e) The time taken, " + (endTime - startTime) + "ns" + "\n");
        System.setOut(console);	
        System.out.println("Done! Check " + outputName);		
	}
}
