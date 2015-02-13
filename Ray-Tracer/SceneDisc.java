import java.util.HashMap;
import java.util.Map;


public class SceneDisc extends SceneGeometricPrimitive implements SceneObjectsInterface {
	
	enum tParts { CENTER, RADIUS, NORMAL, UNKNOWN };	
	Map<String, tParts> stringToPart;
	double[] m_normal = {0}, m_center  = {0};
	double m_radius = 0;	
	
	private double[] m_referenceVector = null;
	private double m_planeCoefficient = 0;
	private double[] m_currentIntersectionPoint = null;
	double[] m_pivotVector = {0};	
	
	public void initObject() 
	{		
		super.initObject();
		
		stringToPart = new HashMap<String, tParts>();
		stringToPart.put("center", tParts.CENTER);
		stringToPart.put("radius", tParts.RADIUS);
		stringToPart.put("normal", tParts.NORMAL);		
	}
		
	public boolean setParameter(Parameter param) throws Exception
	{			
		if (super.setParameter(param)) return true;
		
		tParts part = stringToPart.get(param.getName());
		switch (part)
		{
			case CENTER:
				m_center = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case RADIUS:
				m_radius = Double.parseDouble(param.getValueString());
				break;
			case NORMAL:
				m_normal = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			default:
				System.out.println("Error [SceneDisc::setParameter] : Unkonwn Part [" + param.getName() + "]");
				return false;
		}		
		return true;
	}
	
	public void Finalize()
	{
		super.Finalize();

		m_referenceVector = new double[3];
		// Choose an arbitrary vector as a reference vector - say the x basis vector
		m_referenceVector[0] = 1F;
		m_referenceVector[1] = 0F;
		m_referenceVector[2] = 0F;
		
		// Check if the reference vector is linearly dependent with the direction vector
		if(m_normal[1] == 0 && m_normal[2] == 0)
		{
			// Change the reference vector to something else - say the y basis vector
			m_referenceVector[0] = 0F;
			m_referenceVector[1] = 1F;
			m_referenceVector[2] = 0F;
		}
		
		MathCalcsLib.normalize(m_normal);
		m_planeCoefficient = -(MathCalcsLib.dotProduct(m_normal, m_center));
		m_pivotVector = MathCalcsLib.crossProduct(m_normal, m_referenceVector);
		MathCalcsLib.normalize(m_pivotVector);
	}
	
	public double[] GetNormal(double[] point) {
		return m_normal;
	}
	
	public double FindIntersection(Ray ray) {
		
		double distance = IntersectWithPlane(ray);
		
		if(distance != Double.POSITIVE_INFINITY  &&  distance > 0) {
			return IntersectWithinRadius(ray, distance);			
		}
		
		// TODO: check end cases when ray is exactly on the disc's plane \ even inside the disc
		return Double.POSITIVE_INFINITY;
	}
	
	private double IntersectWithPlane(Ray ray) 
	{	
		double[] raySource = ray.getPosition();
		double[] V = ray.getDirection();
		double distance = Double.POSITIVE_INFINITY;				

		if(MathCalcsLib.dotProduct(V, m_normal) != 0) {
			distance = (-(MathCalcsLib.dotProduct(raySource, m_normal) + m_planeCoefficient)) / MathCalcsLib.dotProduct(V, m_normal);
		}
		
		// TODO: deal with the case that the Disc is exactly parallel to the direction of sight
		if(distance <= 0)
			return Double.POSITIVE_INFINITY;
		return distance;
	}
	
	private double IntersectWithinRadius(Ray ray, double distance) 
	{	
		ray.setMagnitude(distance);
		m_currentIntersectionPoint  = ray.getEndPoint();
		ray.setMagnitude(1);
		
		double distanceFromCenter = MathCalcsLib.norm( MathCalcsLib.calcPointsDiff(m_center, m_currentIntersectionPoint) );

		
		if (distanceFromCenter <= m_radius) {
			return distance;
		}

		return Double.POSITIVE_INFINITY;
	}	
	
	public double[] GetTextureCoords(double[] point) 
	{			 
		double[] centerToPoint = MathCalcsLib.calcPointsDiff(m_center, point);						
		
		double u = MathCalcsLib.norm(centerToPoint) / m_radius;		
				
		MathCalcsLib.normalize(centerToPoint);
		
		double q = MathCalcsLib.dotProduct(m_pivotVector, centerToPoint);
		if (Math.abs(q) > 1) {
			q = 1 * Math.signum(q);
		}
		
		double v = Math.acos(q);		

		//  Check which half of the circle we're at.  If we're in the second, v will be negative, make it positive
		if(MathCalcsLib.dotProduct(MathCalcsLib.crossProduct(m_pivotVector, centerToPoint), m_normal) < 0)
		{
			v = (2 * Math.PI) - v;
		}
		v /= (2 * Math.PI);					
				
		return new double[] { u, v };
	}	
}
