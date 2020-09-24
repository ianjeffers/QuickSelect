package Distributed2;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

class TCPClient {
	private boolean hasPivot;
	private static int[] arr;
	private static int pivot;
	private static int storedIndex;

	public static void main(String argv[]) throws Exception {
		Socket clientSocket = new Socket("10.100.4.161", 6789);
		ObjectInputStream iiStream = new ObjectInputStream(clientSocket.getInputStream());
		arr = (int[]) iiStream.readObject();
		Random rand = new Random();
		int storeIndex = 0;
		while (true) {
			ObjectInputStream iStream = new ObjectInputStream(clientSocket.getInputStream());
			ObjectOutputStream oStream = new ObjectOutputStream(clientSocket.getOutputStream());
			System.out.println("Reading tuple");
			Tuple t = (Tuple) iStream.readObject();
			if (t.needsToGenerateValue()) {
				// Return false if the array is 0
				if (arr.length < 1 || (arr.length == 1 && arr[0] == t.getPivotValue())) {
					oStream.writeBoolean(false);
					oStream.reset();
					System.out.println("Was not able to generate value");
				} else {
					// Return true then return the new pivotvalue if not
					System.out.println("Was able to generate value");
					oStream.writeBoolean(true);
					oStream.reset();
					int randomValue = 0;
					if (t.getKeepSide() == 1) {
						System.out.println("Choosing a random pivot to the right of storeIndex");
						randomValue = rand.nextInt(arr.length) + storedIndex + 1;
						System.out.println("Writing our pivot value out");
						oStream.writeObject(arr[randomValue]);
						oStream.reset();
					} else {
						System.out.println("Choosing a random pivot to the left of storeIndex");
						randomValue = rand.nextInt(storedIndex);
						System.out.println("Writing our pivot value out");
						oStream.writeObject(arr[randomValue]);
						oStream.reset();
					}

				}
				continue;
			}
			if (t.getKeepSide() == 0) {
				// Left
				System.out.println("Keeping left");
				arr = Arrays.copyOfRange(arr, 0, storeIndex);
			} else if (t.getKeepSide() == 1) {
				// Right
				System.out.println("Keeping right");

				arr = Arrays.copyOfRange(arr, storeIndex + 1, arr.length);
			}
			if (t.needsToAppend()) {
				System.out.println("Appending pivot value");
				arr = Arrays.copyOf(arr, arr.length + 1);
				arr[arr.length - 1] = t.getPivotValue();
				pivot = arr.length - 1;
				System.out.println(arr.length + " length at appending");
				System.out.println("Pivot value set to: " + pivot);
			} else {
				System.out.println("Finding pivot value");
				for (int j = 0; j < arr.length; j++) {
					System.out.println("Checking if " + arr[j] + " is equal to: " + t.getPivotValue());
					if (arr[j] == t.getPivotValue()) {
						swap(j, arr.length - 1);
						pivot = arr.length - 1;
						j = arr.length;
					}
				}
			}

			if (arr.length != 0) {
				storeIndex = 0;
				System.out.println(pivot + " / " + arr.length);
				for (int i = 0; i < arr.length; i++) {
					if (arr[i] < arr[pivot]) {
						swap(storeIndex, i);
						storeIndex++;
					}
				}
				swap(arr.length - 1, storeIndex);
			}
			storedIndex = storeIndex;
			oStream.writeObject(storeIndex);
			oStream.reset();
		}
	}

	private static void swap(int one, int two) {
		int temp = arr[one];
		arr[one] = arr[two];
		arr[two] = temp;
	}
}