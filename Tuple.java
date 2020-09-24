package Distributed2;

import java.io.Serializable;

public class Tuple implements Serializable{
	private int keepSide;
	private boolean generateValue;
	private boolean needsAppending;
	private int pivotValue;

	public Tuple(int keepSide, boolean generateValue, boolean needsAppending, int pivotValue) {
		this.keepSide = keepSide;
		this.generateValue = generateValue;
		this.needsAppending = needsAppending;
		this.pivotValue = pivotValue;
	}

	public int getKeepSide() {
		return keepSide;
	}

	public void setKeepSide(int keepSide) {
		this.keepSide = keepSide;
	}

	public boolean needsToGenerateValue() {
		return generateValue;
	}

	public void setGenerateValue(boolean generateValue) {
		this.generateValue = generateValue;
	}

	public boolean needsToAppend() {
		return needsAppending;
	}

	public void setNeedsAppending(boolean needsAppending) {
		this.needsAppending = needsAppending;
	}

	public int getPivotValue() {
		return pivotValue;
	}

	public void setPivotValue(int pivotValue) {
		this.pivotValue = pivotValue;
	}
}
