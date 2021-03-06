
// Static mathematical utility class for linear algebra and other things
public class MathCalcsLib {
	
	public static double sqr(double a) {
		return a * a;
	}
	
	// Returns the square of a - b
	public  static double sqrDiff(double a, double b) {
		return sqr(a - b);
	}
	
	// Vector addition, adds addition to vec
	public static double[] addVector(double[] vec, double addition[]) 
	{		
		vec[0] += addition[0]; 
		vec[1] += addition[1];
		vec[2] += addition[2];
		return vec;
	}
	
	// Multiplies addition by a scalar and then adds the result to vec
	public static double[] addVectorAndMultiply(double[] vec, double addition[], double scalar) 
	{
		vec[0] += addition[0] * scalar; 
		vec[1] += addition[1] * scalar;
		vec[2] += addition[2] * scalar;
		return vec;
	}
	
	
	public static double[] multiplyVectorByScalar(double[] vec, double scalar) {
		vec[0] *= scalar; 
		vec[1] *= scalar;
		vec[2] *= scalar;
		return vec;
	}
		
	/**
	 * Calculates a dot product between two given vectors
	 * @param vec1
	 * @param vec2
	 * @return
	 */
	//inner multiply product
	public static double dotProduct(double[] vec1, double[] vec2) {
		double result = (vec1[0] * vec2[0])  +  (vec1[1] * vec2[1])  +  (vec1[2] * vec2[2]) ;
		return result;
	}	
	
	/**
	 * Calculates the differnce between two point in 3D space
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double[] calcPointsDiff(double[] p1, double[] p2) {
		return new double [] { p2[0] - p1[0] , p2[1] - p1[1] , p2[2] - p1[2] };
	}

	// Returns the norm of the difference between this vector's position point and another point
	public static double norm(double[] p) {					
		return Math.sqrt(sqr(p[0]) + sqr(p[1]) + sqr(p[2]));
	}

	// Normalizes a vector
	public static double[] normalize(double[] vec) {
		double norm = norm(vec);		
		if (norm == 0) return new double[]{0.0,0.0,0.0};		
		vec[0] /= norm;
		vec[1] /= norm;
		vec[2] /= norm;
		return vec;
	}

	// Returns the cross product of 2 vectors
	public static double[] crossProduct(double[] d1, double[] d2) {				
		double[] result = { (d1[1] * d2[2]) - (d1[2] * d2[1]), (d1[2] * d2[0]) - (d1[0] * d2[2]), (d1[0] * d2[1]) - (d1[1] * d2[0]) };	
		return result;  		
	}	
	
	// Reflects a vector around a normal vector. both vectors are assumed to have the same shift from the origin
	public static double[] reflectVector(double[] vec, double[] normal) {
		double dotProduct = dotProduct(vec, normal);		
		double[] r = new double[] { -vec[0] + 2 * normal[0] * dotProduct,
								  -vec[1] + 2 * normal[1] * dotProduct,
								  -vec[2] + 2 * normal[2] * dotProduct };						
		return r;
	}
	
	// Returns the vector opposite to vec
	public static double[] oppositeVector(double[] vec) {				
		double[] result = new double[] { -vec[0], -vec[1], -vec[2] };						
		return result;
	}
	
	/**
	 * Given three points, this method returns true if they are collinear, and false otherwise.
	 * @param p0
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static boolean arePointsCollinear(double[] p0, double[] p1, double[] p2) {

		// coefficients for testing collinearity
		double a,b,c;		
		// Define the vectors between pairs of the given points 
		double[] vec1 = { p1[0] - p0[0] , p1[1] - p0[1] , p1[2] - p0[2] };
		double[] vec2 = { p2[0] - p0[0] , p2[1] - p0[1] , p2[2] - p0[2] };
		a = vec1[0] / vec2[0];
		b = vec1[1] / vec2[1];
		c = vec1[2] / vec2[2];
		// If all coefficients are equal then some scalar exists which scales between the vectors
		// e.g. they are linearly dependent and all 3 points are on the same line
		if (a == b  && b == c) return true;		
		return false;
	}

	
	/**
	 * Solves a quadratic equation with coefficients a, b, c. <br /> 
	 * Returns Double.POSITIVE_INFINITY if no roots exist. <br />
	 * Returns (-b) / (2 * a) if only one root exists. <br />
	 * Returns the minimum root if two roots exist.
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static double[] solveQuadraticEquation(double a, double b, double c) {
		
		double[] roots = new double[2];
		if(a == 0)
		{
			roots[0] = -c / b;
		}
		else
		{
			double discriminant = sqr(b) - 4 * a * c;
	
			if (discriminant < 0)
			{
				roots[0] = Double.POSITIVE_INFINITY;
			}
			else if(discriminant == 0)
			{
				roots[0] =  (-b) / (2 * a);
			}
			else
			{
				discriminant = Math.sqrt(discriminant);
				double denominator = 2 * a;
				roots[0] = (-b + discriminant) / (denominator);
				roots[1] = (-b - discriminant) / (denominator);	
			}
		}		
		return roots;
	}

	/**
	 * Add two points in 3D
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[] addPoints(double[] a, double[] b) {
		double[] c = { a[0] + b[0] , a[1] + b[1] , a[2] + b[2] };
		return c;
	}
	
	public static double[] subtractPoints(double[] a, double[] b) {
		double[] c = { a[0] - b[0] , a[1] - b[1] , a[2] - b[2] };
		return c;
	}

}
