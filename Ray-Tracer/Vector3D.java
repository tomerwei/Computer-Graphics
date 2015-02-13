
// Vector class for vectors having a position, direction and magnitude properties
public class Vector3D {
	double[] position;
	double[] direction;	
	double magnitude;
	
	public Vector3D(double[] position, double[] direction, double magnitude) {
		this.position = position;
		this.direction = direction;
		this.magnitude = magnitude;
	}
	
	// Returns the norm of the difference between this vector's position point and another point
	public double normPointDiff(double[] p2) {
		double[] p1 = this.position;				
		return Math.sqrt(MathCalcsLib.sqrDiff(p1[0], p2[0]) + MathCalcsLib.sqrDiff(p1[1], p2[1]) + MathCalcsLib.sqrDiff(p1[2], p2[2]));
	}
		
	// Normalizes the vector
	public void normalize() {
		double norm  = MathCalcsLib.norm(direction);		
		this.direction[0] = direction[0] / norm;
		this.direction[1] = direction[1] / norm;
		this.direction[2] = direction[2] / norm;		
		this.magnitude = 1;	
	}
	
	// Returns the dot product of the current vector's direction with the other vector's direction
	public double dotProduct(Vector3D otherVec) {
		return (this.direction[0] * otherVec.direction[0] + 
				this.direction[1] * otherVec.direction[1] +
				this.direction[2] * otherVec.direction[2]);
	}
	
		
	// Reflects a vector around a normal. assumes the normal vector is normalized
	public void reflectAround(double[] normal) {
		if (magnitude != 1) normalize();		
		double dotProduct = MathCalcsLib.dotProduct(direction, normal);		
		this.direction[0] = -direction[0] + 2 * normal[0] * dotProduct;
		this.direction[1] = -direction[1] + 2 * normal[1] * dotProduct;
		this.direction[2] = -direction[2] + 2 * normal[2] * dotProduct;
	}
	
	
	// Returns the end of the vector as a point in 3D space 
	public double[] getEndPoint() {
		double[] endPoint = { position[0] + magnitude * direction[0], 
							 position[1] + magnitude * direction[1], 
							 position[2] + magnitude * direction[2] };		
		return endPoint;
	}
	
	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	public double[] getDirection() {
		return direction;
	}

	public void setDirection(double[] direction) {
		this.direction = direction;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}		
}
