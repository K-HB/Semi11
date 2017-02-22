package de.khb.semi3d.computation;

public interface TimeFunction{
	public static class ConstantFunction implements TimeFunction{
		
		private final double current;
		
		public ConstantFunction(double current) {
			this.current = current;
		}
		
		@Override
		public double nextValue() {
			return current;
		}

		@Override
		public double firstValue() {
			return current;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(current);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConstantFunction other = (ConstantFunction) obj;
			if (Double.doubleToLongBits(current) != Double.doubleToLongBits(other.current))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "ConstantFunction [current=" + current + "]";
		}
	}
	
	public double firstValue();
	public double nextValue();
	
	public boolean equals(Object o);
	public int hashCode();
}