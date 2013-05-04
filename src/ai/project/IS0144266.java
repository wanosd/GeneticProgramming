package ai.project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IS0144266 {

	static ArrayList<Generation> gens = new ArrayList<Generation>();

	public static void main(String args[]){
		if (args.length != 7){
			System.out.print("-Invalid Parameters-\nFormat is: java IS0144266 popSize generations crossoverType crossOverRate mutationType mutationRate filename");
			System.exit(0);
		}
		int popSize = Integer.parseInt(args[0]); //variable for population size
		int generations = Integer.parseInt(args[1]); //variable for number of generations
		String crossoverType = args[2]; // variable for Crossover Type
		double crossoverRate = Double.parseDouble(args[3]); // variable for Crossover Rate
		String mutationType = args[4]; // variable for Mutation Type
		double mutationRate = Double.parseDouble(args[5]); // variable for mutation rate
		String filename = args[6];
		long seed = 0144267;
		int choice[];
		int genNo = 0;
		int tSize = 10;
		String[] indPath = new String[popSize];
		String[] indDist = new String[popSize];
		String[] nextGenP = new String[popSize];
		String[] nextGenD = new String[popSize];
		String firstPop = "\n****\nFirst Population\n****\n"; //String used if you want to write the first population to the file to ensure it's all good.

		/**
		 * Populate arrayList to remove null references
		 */
		for (int i = 0; i<indPath.length; i++){
			indPath[i] ="";
		}

		String output = "*****\nCommand Entry Args: " + popSize + ", " + generations + ", " + crossoverType + ", " + crossoverRate + "," + mutationType + ", " + mutationRate + ", " + filename + "\nSeed: " + seed + "\n*****"; 
		ArrayList<City> baseInd = new ArrayList<City>(); // the baseInd used for recalculated distances.

		/**
		 * Steps
		 * 1. Create List of 100 cities.
		 * 2. Get Path, Distance
		 * 3. Shuffle cities, get p/d, add to population
		 */
		writeHeader(filename, output);
		createBaseInd(baseInd);
		Random r = new Random(seed);
		for (int i = 0; i < popSize; i++){
			Collections.shuffle(baseInd, r);
			for (int j = 0; j < baseInd.size(); j++){
				indPath[i] += String.valueOf(baseInd.get(j).getID() + ".");
			}
			indDist[i] = Double.toString(calculateDistance(baseInd));
		}
		//writeFile(filename, indPath, indDist, firstPop); Uncomment to write the intial population to the file

		// Tournament size is 10
		//start for
		for (int genStart = 0; genStart < generations; genStart++){ // starts generation

			for (int i = 0; i< popSize; i++){
				choice = tournamentSelection(tSize, popSize); // gets 10 random numbers
				String[] tourPath = new String[choice.length]; //creates new tourPath of length 10
				String[] tourDist = new String[choice.length]; //this is for distance

				/**
				 * Creating mating pool of tSize items
				 */
				for (int j = 0; j<choice.length; j++){
					tourPath[j] = indPath[choice[j]]; // populates the mating pool with the random 10 choices
					tourDist[j] = indDist[choice[j]];
				}

				int shortest = shortestDistance(tourDist); // picks the shortest tour from the mating pool
				//code here to choose between crossover, mutate, reproduction
				double randomChoice = 0 + (double)(Math.random() * 1); //random number between 0 and 1.
				//double randomChoice = 1;
				if (randomChoice <= mutationRate){
					nextGenP[i] = mutate(tourPath, shortest, mutationType); // 
					nextGenD[i] = String.valueOf(recalcDistance(baseInd, nextGenP[i]));
				}

				else if ((randomChoice > mutationRate) && (randomChoice <= crossoverRate)){
					nextGenP[i] = crossover(tourPath, crossoverType); // change this crossover method once implemented
					nextGenD[i] = String.valueOf(recalcDistance(baseInd, nextGenP[i]));
					
				}

				else if (randomChoice > crossoverRate){
					nextGenP[i] = tourPath[shortest];
					nextGenD[i] = tourDist[shortest];
				}


				if (i == popSize-1){
					genNo++;
					System.out.println("Generation " + genNo + " starting"); //Prints to command line to let you know what gen is running
					gens.add(writeGenPath(nextGenP, nextGenD, filename, genNo)); //Writes a record of each gen to the Generation object
					indPath = nextGenP; // makes sure that the generation created is used in the next generation (not the tv show)
					indDist = nextGenD;
				}

			}
			if (genNo == generations){
				writeFinal(gens, filename); //writes generation results to the file. Yup, all of them in one go.
			}
		}

	}

	/**
	 * method to writer the head, with command line, seed value etc to the file
	 * @param filename
	 * @param output
	 */
	public static void writeHeader(String filename, String output){
		FileWriter out;
		try
		{
			out = new FileWriter(filename);
			out.write(output + "\n");
			out.close();
		}
		catch (Exception e){
			System.out.print("File cannot be found/edited");
		}
	}

	/**
	 * Old writeFile method. Not used during the program run. It's not doing any harm here though. Leave him alone.
	 * @param filename
	 * @param path
	 * @param dist
	 * @param message
	 */
	public static void writeFile(String filename, String[] path, String[] dist, String message){
		FileWriter out;
		try
		{
			out = new FileWriter(filename, true);
			out.write(message);
			for (int i = 0; i<path.length; i++){
				out.write("\n" + i + ": " + path[i] + "\nDistance: " + dist[i] + "\n");
			}
			out.close();
		}
		catch (Exception e){
			System.out.print("File cannot be found/edited");
		}
	}

	/**
	 * Method to create the list of City objects from the towns.csv file.
	 * @param baseInd
	 * @return
	 */
	public static ArrayList<City> createBaseInd(ArrayList<City> baseInd){

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("towns.csv"));
			String line;
			String[] lines;

			while ((line = reader.readLine()) != null)
			{
				lines = line.split(",");
				baseInd.add(new City(lines[0], Double.parseDouble(lines[1]), Double.parseDouble(lines[2]), Integer.parseInt(lines[3])));
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("file not found");
			e.printStackTrace();
		}

		return baseInd;
	}

	/**
	 * Methods for calculating and recalculating the distances
	 * @param ind
	 * @return
	 */
	public static double calculateDistance(ArrayList<City> ind){
		double totalDistance = 0;
		for (int i = 0; i<ind.size() - 1; i++){
			totalDistance += distance(i, i+1, ind);
		}
		totalDistance += distance(99, 0, ind);
		//	System.out.print("total: " + totalDistance);
		return totalDistance;
	}

	public static double distance(int x, int y, ArrayList<City> baseInd){
		double dx, dy;
		dx = (baseInd.get(x).getX()) - (baseInd.get(y).getX());
		dy = (baseInd.get(x).getY()) - (baseInd.get(y).getY());
		return Math.sqrt(dx*dx + dy*dy);
	}

	public static int shortestDistance(String[] dist){
		double shortest = Double.parseDouble(dist[0]);
		int location = 0;
		for (int i = 1; i<dist.length; i++){
			if (shortest > Double.parseDouble(dist[i])){
				shortest = Double.parseDouble(dist[i]);
				location = i;
			}
		}

		return location;
	}

	/**
	 * Gives you 10 random members for the mating pool selection, default is 10
	 * @param size
	 * @param popSize
	 * @return
	 */
	public static int[] tournamentSelection(int size, int popSize){
		int choice[] = new int[size];
		for (int i = 0; i<size; i++){
			choice[i] = (int)(Math.random()*(100));
		}
		return choice;
	}

	/**
	 * Also not used. It was used but was replaced by the Generation class. So technically, it's the last generation.
	 * @param path
	 * @param dist
	 * @param filename
	 */
	public static void writeAvgFitness(String[] path, String[] dist, String filename){
		FileWriter out;
		double avgFit = 0;
		try
		{
			out = new FileWriter(filename, true);
			for (int i = 0; i<path.length; i++){
				avgFit += Double.parseDouble(dist[i]);
			}
			out.write("\nAvg Fitness: " + avgFit/path.length);
			out.close();
		}
		catch (Exception e){
			System.out.print("File cannot be found/edited");
		}
	}

	/**
	 * Creates new generation for the gens object.
	 * @param path
	 * @param dist
	 * @param filename
	 * @param genNo
	 * @return
	 */
	public static Generation writeGenPath(String[] path, String[] dist, String filename, int genNo){
		int shortest = 0;
		double average = 0;
		shortest = shortestDistance(dist);
		for (int i = 0; i < dist.length; i++){
			average += Double.valueOf(dist[i]);
		}
		return new Generation(genNo, path[shortest], dist[shortest], (average/path.length));
	}


	/**
	 * Method for the mutation
	 * @param tourPath
	 * @param param
	 * @return
	 */
	public static String mutate(String[] tourPath, int shortest, String param){
		String line[] = tourPath[shortest].split("\\.");
		String ret = "";
		int pos1 = 0, pos2 = 0;
		do{
			pos1 = (int) (Math.random() * (line.length));
			pos2 = (int) (Math.random() * (line.length));
		} while (pos1 == pos2);
		
		//mutuation based on exchange
		if (param.equals("e")){
			String temp = line[pos1];
			//System.out.println("\n\n" + pos1 +"," + pos2);
			line[pos1] = line[pos2];
			line[pos2] = temp;

			for (int i = 0; i<line.length; i++){
				ret += line[i] + ".";
			}
			return ret;
		}

		//mutation based on inversion
		if (param.equals("i")){

			if (pos1 > pos2){
				int temp = pos1;
				pos1 = pos2;
				pos2 = temp;
			}

			while (pos1 < pos2){
				String temp = line[pos1];
				line[pos1] = line[pos2];
				line[pos2] = temp;
				pos1++; pos2--;
			}
			for (int i = 0; i<line.length; i++){
				ret += line[i] + ".";
			}
			return ret;
		}
		// Insertion Mutation
		else{
			if (pos1 > pos2){
				int temp = pos1;
				pos1 = pos2;
				pos2 = temp;
			}
			String insert = line[pos1];
			while (pos1 != pos2){
				line[pos1] = line[pos1+1];
				pos1++;
			}	
			line[pos2] = insert;
			for (int i = 0; i<line.length; i++){
				ret += line[i] + ".";
			}
			return ret;
		}

	}

	/**
	 * Method for the Crossover function
	 * @param tourPath
	 * @param param
	 * @return
	 */
	public static String crossover(String[] tourPath, String param){
		String children = "";
		String[] parent1 = tourPath[(int)(Math.random()*(tourPath.length))].split("\\.");
		String[] parent2 = tourPath[(int)(Math.random()*(tourPath.length))].split("\\.");

		ArrayList<String> child1 = new ArrayList<String>();

		// alternating point crossover
		if (param.equals("a")){
			for (int i = 0; i< parent1.length; i++){
				if (exists(child1, parent1[i]) == false){
					child1.add(parent1[i]);
				}	

				if (exists(child1, parent2[i]) == false){
					child1.add(parent2[i]);
				}
			}

			for (int i = 0; i < child1.size(); i++){
				children += child1.get(i) + ".";
			}
			return children;
		}

		//OX1 Crossover	
		else if (param.equals("o"))
		{
			for (int i = 0; i < parent1.length; i++){
				child1.add("-1");
			}
			int pos1 = 0, pos2 = 0;
			do{
				pos1 = (int) (Math.random() * (parent1.length));
				pos2 = (int) (Math.random() * (parent1.length));
				if (pos1 > pos2){
					int temp = pos1;
					pos1 = pos2;
					pos2 = temp;
				}
			} while (pos1 == pos2);

			//set between pos1 and pos2 the values between parent1[pos1] and parent1[pos2]
			for (int i = (pos1); i <= pos2; i++){
				child1.set(i, parent1[i]);
			}

			for (int i = pos2; i < 100; i++) {
				if (exists(child1, parent2[i]) == false) {
					for (int j = pos2; j < 100; j++) {
						if (child1.get(j).equals("-1")) {
							child1.set(j, parent2[i]);
							break;
						}
					}
				}
			}
			// Loop from 0 to endPos and check if town exists. If it doesn't find an empty position between endPos and 100 ELSE if no empty position between endPos and 100 loop from 0 to 100 for an empty position
			for (int i = 0; i < pos2; i++) {
				if (exists(child1, parent2[i]) == false) {
					for (int j = pos2; j < 100; j++) {
						if (child1.get(j).equals("-1")) {
							child1.set(j, parent2[i]);
							break;
						}
					}
					if(!(child1.get(99).equals("-1")) && !(child1.get(99).equals(parent2[i]))){
						for (int j = 0; j < 100; j++) {
							if (child1.get(j).equals("-1")) {
								child1.set(j, parent2[i]);
								break;
							}
						}
					}
				}
			}

			for (int j = 0; j<100; j++){
				if (child1.get(j).equals("-1")){
					for (int i = 0; i < 100; i++){
						if (exists(child1, parent2[i]) == false){
							child1.set(j, parent2[i]);
						}
					}
				}


			}

			for (int k = 0; k<child1.size(); k++){
				children += child1.get(k) + ".";
			}
			return children;

		}


		//Maximal Preservative Crossover
		else if (param.equals("m")){
			int pos1 = 0, pos2 = 0;
			do{
				pos1 = 10 + (int) (Math.random() * (parent1.length));
				pos2 = (pos1 + (int) (Math.random() * (parent1.length))/2) + 10;

			} while (pos1 == pos2 || (pos1 + pos2) > 100);

			for (int i = pos1; i< pos2; i++){
				child1.add(parent1[i]);
			}

			for (int j = 0; j < parent2.length; j++){
				if (exists(child1, parent2[j]) == false){
					child1.add(parent2[j]);
				}
			}
			for (int i = 0; i < child1.size(); i++){
				children += child1.get(i) + ".";
			}
			return children;
		}

		return children;
	}

	/**
	 * Method to recalculate a distance between cities
	 * @param cities
	 * @param path
	 * @return
	 */
	public static double recalcDistance(ArrayList<City> cities, String path){

		double distance = 0;
		String[] split = path.split("\\.");
		ArrayList<City> temp = new ArrayList<City>();
		for (int i = 0; i<split.length; i++){
			for (int j = 0; j < cities.size(); j++){
				if (cities.get(j).getID() == Integer.parseInt(split[i])){
					temp.add(cities.get(j));
				}
			}
		}
		distance = calculateDistance(temp);
		return distance;
	}

	/**
	 * Checking if a value exists in a potention path
	 * @param child
	 * @param value
	 * @return
	 */
	public static Boolean exists(ArrayList<String> child, String value){
		for (int i = 0; i< child.size(); i++){
			if (child.get(i).equals(value)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Creates final file.
	 * @param gens
	 * @param filename
	 */
	public static void writeFinal(ArrayList<Generation> gens, String filename){
		FileWriter out;
		int shortestGen = 0;
		try
		{
			out = new FileWriter(filename, true);
			for (int i = 0; i<gens.size(); i++){
				out.write("\n" + "Gen " + gens.get(i).getGenNo() + ": Best Distance: (" + gens.get(i).getBestD() + "), Average: (" + gens.get(i).getAverage() + ") Path was (" + gens.get(i).getBestP() + ")");
				if (Double.valueOf(gens.get(i).getBestD()) < Double.valueOf(gens.get(shortestGen).getBestD())){
					shortestGen = i;
				}

			}
			out.write("\n\nThe Very Best of the Best Was");
			out.write("\n" + "Gen " + gens.get(shortestGen).getGenNo() + ": Best Distance: (" + gens.get(shortestGen).getBestD() + "), Average: (" + gens.get(shortestGen).getAverage() + ") Path was (" + gens.get(shortestGen).getBestP() + ")");
			double avg = 0;
			for (int i = 0; i< gens.size(); i++){
				avg += gens.get(i).getAverage();
			}
			out.write("\nAverage of all " + gens.size() + " generations: " + (avg/gens.size()) );
			out.close();
		}
		catch (Exception e){
			System.out.print("File cannot be found/edited");
		}
	}

}
