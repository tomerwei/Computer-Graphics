import java.util.HashMap;
import java.util.Map;

public class SceneRectangle extends SceneGeometricPrimitive implements SceneObjectsInterface {
	
	enum tParts { P0, P1, P2, UNKNOWN };	
	Map<String, tParts> stringToPart;

	double[] m_p0={0}, m_p1={0}, m_p2={0}, m_p3={0};

	private double[] m_AB = {0}, m_AC = {0};
	private double m_ABdotAB = 0, m_ACdotAC = 0;
	private double m_ABnorm = 0, m_ACnorm = 0;

	
	private double[] m_normal = null;
	private double m_planeCoefficient = 0;
	private double[] m_currentIntersectionPoint = {0};
	
	public SceneRectangle()
	{
		
	}
	
	public SceneRectangle(double[] p0, double[] p1, double[] p2)
	{
		m_p0 = p0;
		m_p1 = p1;
		m_p2 = p2;
		Finalize();
	}
	
	public void initObject() 
	{
		super.initObject();
		
		stringToPart = new HashMap<String, tParts>();
		stringToPart.put("p0", tParts.P0);
		stringToPart.put("p1", tParts.P1);
		stringToPart.put("p2", tParts.P2);		
	}
	
	public boolean setParameter(Parameter param) throws Exception
	{	
		if (super.setParameter(param)) return true;
		
		tParts part = stringToPart.get(param.getName());
		switch (part)
		{
			case P0:
				m_p0 = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case P1:
				m_p1 = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case P2:
				m_p2 = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			default:
				System.out.println("Error [SceneRectangle::setParameter] : Unkonwn Part [" + param.getName() + "]");
				return false;
		}		
		return true;
	}
		
	public void Finalize() 
	{
		super.Finalize();
		
		if (MathCalcsLib.arePointsCollinear(m_p0, m_p1, m_p2)) {
			return;
		}
		double[] newPoint = { m_p1[0] + (m_p2[0] - m_p0[0]) , m_p1[1] + (m_p2[1] - m_p0[1]) , m_p1[2] + (m_p2[2] - m_p0[2]) };		
		m_p3 = newPoint;	
		
		m_normal = MathCalcsLib.crossProduct(MathCalcsLib.calcPointsDiff(m_p0, m_p1),
				   MathCalcsLib.calcPointsDiff(m_p0, m_p2));
		MathCalcsLib.normalize(m_normal);
		
		m_planeCoefficient = -(MathCalcsLib.dotProduct(m_normal, m_p0)); 	
		
		m_AB = MathCalcsLib.calcPointsDiff(m_p0, m_p1);
		m_ABdotAB = MathCalcsLib.dotProduct(m_AB, m_AB);
		m_AC = MathCalcsLib.calcPointsDiff(m_p0, m_p2);
		m_ACdotAC = MathCalcsLib.dotProduct(m_AC, m_AC);
		m_ABnorm = MathCalcsLib.norm(m_AB);
		m_ACnorm = MathCalcsLib.norm(m_AC);		
	}
		
	public double[] GetNormal(double[] point) {
		return m_normal;
	}

	public double FindIntersection(Ray ray) 
	{	
		double distance = intersectWithPlane(ray);
		
		if(distance != Double.POSITIVE_INFINITY  &&  distance != Double.NEGATIVE_INFINITY) {
			return intersectBarycentric(ray, distance);			
		}
		
		// TODO: check end cases when ray is exactly on the disc's plane \ even inside the disc
		return Double.POSITIVE_INFINITY;
	}	
	
	private double intersectWithPlane(Ray ray) 
	{	
		double[] raySource = ray.getPosition();
		double[] V = ray.getDirection();
		double distance = 0;

		if(MathCalcsLib.dotProduct(V, m_normal) != 0) {  
			distance = (-(MathCalcsLib.dotProduct(raySource, m_normal) + m_planeCoefficient)) / MathCalcsLib.dotProduct(V, m_normal);
		}
		
		if(distance <= 0) {
			return Double.POSITIVE_INFINITY;
		}
		return distance;
	}
	
	private double intersectBarycentric(Ray ray, double distance) 
	{	
		double[] v0, v1, v2;
		double dot00, dot01, dot02, dot11, dot12;
		double denominator, u, v;
				
		// Get the intersection point with the rectangle's plane
		ray.setMagnitude(distance);
		m_currentIntersectionPoint = ray.getEndPoint();

		// Compute vectors        
		v0 = MathCalcsLib.calcPointsDiff(m_p0, m_p2);
		v1 = MathCalcsLib.calcPointsDiff(m_p0, m_p1);
		v2 = MathCalcsLib.calcPointsDiff(m_p0, m_currentIntersectionPoint);

		// Compute dot products
		dot00 = MathCalcsLib.dotProduct(v0, v0);
		dot01 = MathCalcsLib.dotProduct(v0, v1);
		dot02 = MathCalcsLib.dotProduct(v0, v2);
		dot11 = MathCalcsLib.dotProduct(v1, v1);
		dot12 = MathCalcsLib.dotProduct(v1, v2);

		// Compute barycentric coordinates
		denominator = 1 / (dot00 * dot11 - dot01 * dot01);
		u = (dot11 * dot02 - dot01 * dot12) * denominator;
		v = (dot00 * dot12 - dot01 * dot02) * denominator;

		// Check if point is in rectangle
		if ((u > 0) && (v > 0) && (u < 1) && (v < 1)) {
			return distance;
		}
		
		return Double.POSITIVE_INFINITY;
	}
	
	public double[] GetTextureCoords(double[] point) {
		
		double[] AP;
						
		AP = MathCalcsLib.calcPointsDiff(m_p0, point);
		double q = 1 / MathCalcsLib.norm(MathCalcsLib.calcPointsDiff(m_p0, m_p1));
				
		double u = MathCalcsLib.dotProduct(m_AB, AP) / m_ABdotAB;
		double v = MathCalcsLib.dotProduct(m_AC, AP) / m_ACdotAC;
		
		u /= m_ABnorm * q;
		v /= m_ACnorm * q;
		
		return new double[] { u, v };
	}
}
