package Distributed2;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

class TCPServer {
	// Need an object to tell clients to keep a specific side, if they need to
	// generate a pivot value, if they need to append, and what pivot value to
	// append

	private static int[] arr;

	public static void main(String args[]) throws Exception {
		int sum = 0;
		ServerSocket serverSocket = new ServerSocket(6789);
		int numClients = 2;
		int[] a = { 13, 2, 4, 5, 24, 12, 14, 6, 9 };
		arr = a;
		int pivot = 4;
		int pivotValue = arr[pivot];
		int clientHoldingPivot = 0;
		int chunkSize = a.length / numClients;
		if (chunkSize < 1) {
			chunkSize = 1;
		}

		ArrayList<Socket> socketList = new ArrayList<Socket>();
		for (int i = 0; i < numClients; i++) {
			int left = i * chunkSize;
			int right = (i + 1) * chunkSize;
			if (i == (numClients - 1)) {
				right = arr.length;
			}
			if (pivot >= left && pivot < right) {
				clientHoldingPivot = i;
			}
			Socket clientSocket = serverSocket.accept();
			socketList.add(clientSocket);
			System.out.println("Client Connected!");
			ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
			os.writeObject(Arrays.copyOfRange(arr, left, right));
			System.out.println("Array written from " + left + " to " + right);

		}

		// Need an object to tell clients to keep a specific side, if they need to
		// generate a pivot value, if they need to append, and what pivot value to
		// append
		boolean first = true;
		while (true) {
			if (first) {
				for (int i = 0; i < socketList.size(); i++) {
					ObjectOutputStream oStream = new ObjectOutputStream(socketList.get(i).getOutputStream());
					if (clientHoldingPivot == i) {
						oStream.writeObject(new Tuple(5, false, false, pivotValue));
						System.out.println("Writing to client with the pivot");
					} else {
						oStream.writeObject(new Tuple(5, false, true, pivotValue));
						System.out.println("Writing to client without the pivot");
					}
				}
				first = false;
			} else if (sum > pivot) {
				System.out.println("Sum > pivot");
				boolean foundNewPivot = false;
				for (int i = 0; i < socketList.size(); i++) {
					System.out.println("Beginning iteration");
					ObjectOutputStream oStream = new ObjectOutputStream(socketList.get(i).getOutputStream());
					if (!foundNewPivot) {
						System.out.println("Hasn't found new pivot, writing new tuple");
						oStream.writeObject(new Tuple(0, true, false, pivotValue));
						//int keepSide, boolean generateValue, boolean needsAppending, int pivotValue
						ObjectInputStream iStream = new ObjectInputStream(socketList.get(i).getInputStream());
						foundNewPivot = iStream.readBoolean();
						System.out.println("Found new pivot, getting index");
						if (foundNewPivot) {
							ObjectInputStream iiStream = new ObjectInputStream(socketList.get(i).getInputStream());
							System.out.println("Found pivot, resetting loop");
							pivotValue = (Integer) iiStream.readObject();
							clientHoldingPivot = i;
							i = -1;
						}
					} else {
						System.out.println("Found new pivot");
						// int keepSide, boolean generateValue, boolean needsAppending, int pivotValue
						if (clientHoldingPivot == i) {
							System.out.println("Sending to client holding pivot, sum > pivot");
							oStream.writeObject(new Tuple(0, false, false, pivotValue));
						} else {
							System.out.println("Sending to client not holding pivot, sum > pivot");
							oStream.writeObject(new Tuple(0, false, true, pivotValue));
						}
					}
				}
			} else if (sum < pivot) {
				System.out.println("Sum < pivot");
				boolean foundNewPivot = false;
				System.out.println(socketList.size() + " size");
				for (int i = 0; i < socketList.size(); i++) {
					ObjectOutputStream oStream = new ObjectOutputStream(socketList.get(i).getOutputStream());
					ObjectInputStream iStream = new ObjectInputStream(socketList.get(i).getInputStream());
					if (!foundNewPivot) {
						oStream.writeObject(new Tuple(1, true, false, pivotValue));
						foundNewPivot = iStream.readBoolean();
						
						System.out.println("Searching for pivot");
						if (foundNewPivot) {
							pivotValue = iStream.readInt();
							
							clientHoldingPivot = i;
							i = -1;
							System.out.println("Found new pivot, resetting");
						}
					} else {
						if (clientHoldingPivot == i) {
							System.out.println("Sending to client holding pivot, sum < pivot");

							oStream.writeObject(new Tuple(1, false, false, pivotValue));
							
						} else {
							System.out.println("Sending to client holding pivot, sum < pivot");

							oStream.writeObject(new Tuple(1, false, true, pivotValue));
						}
					}
				}
			} else {
				System.out.println(pivotValue + " is the nth smallest number");
				break;
			}

			for (int i = 0; i < socketList.size(); i++) {
				System.out.println("Incrementing sum");
				ObjectInputStream iStream = new ObjectInputStream(socketList.get(i).getInputStream());
				int temp = (Integer) iStream.readObject();
				System.out.println("temp " + temp);
				sum += temp;
				System.out.println("Sum: " + sum);
			}
		}
	}
}
