package com.ChaseHQ.Statistician.Utils;

public final class Position {
	
		int x;
		int y;
	
		int z;
	
		public Position (int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
	
		}

		public int getY() {
			return y;
		}
		public int getX() {
			return x;
		}

		@Override
		public String toString() {
			return "Position [x=" + x + ", y=" + y + ", z=" + z + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
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
			Position other = (Position) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			return true;
		}
		
		
	
}
