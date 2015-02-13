import java.util.HashMap;
import java.util.Map;

public class SceneCylinder extends SceneGeometricPrimitive implements SceneObjectsInterface {

	enum tParts { START, DIRECTION, LENGTH, RADIUS, UNKNOWN };	
	Map<String, tParts> stringToPart;
	
	private double[] m_start = {0}, m_direction = {0};
	private double m_length = 0, m_radius = 0;
	
	private double[] m_referenceVector = {0};
	private double[] m_pivotVector = {0};
	private double[] m_PointsDiff = {0};
	private double m_PointsDiffDot = 0;
	
	public void initObject() 
	{
		super.initObject();
		
		stringToPart = new HashMap<String, tParts>();
		stringToPart.put("start", tParts.START);
		stringToPart.put("direction", tParts.DIRECTION);
		stringToPart.put("length", tParts.LENGTH);		
		stringToPart.put("radius", tParts.RADIUS);		
	}
	
	public boolean setParameter(Parameter param) throws Exception
	{
		if (super.setParameter(param)) return true;
		
		tParts part = stringToPart.get(param.getName());
		switch (part)
		{
			case START:
				m_start = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case DIRECTION:
				m_direction = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			case LENGTH:
				m_length = Double.parseDouble(param.getValueString());
				break;
			case RADIUS:
				m_radius = Double.parseDouble(param.getValueString());
				break;
			default:
				System.out.println("Error [SceneCylinder::setParameter] : Unkonwn Part [" + param.getName() + "]");
				return false;
		}		
		return true;
	}
	
	public void Finalize()
	{
		super.Finalize();

		// Choose x-basis for reference vector
		m_referenceVector = new double[] { 1F, 0F, 0F };
		if (MathCalcsLib.dotProduct(m_referenceVector, m_direction) > 0.9)
			m_referenceVector = new double[] { 0F, 1F, 0F };
		MathCalcsLib.normalize(m_referenceVector);
		MathCalcsLib.normalize(m_direction);
		m_pivotVector = MathCalcsLib.crossProduct(m_direction, m_referenceVector);
		MathCalcsLib.normalize(m_pivotVector);
		
		double[] end = new double[3];
		end[0] = m_start[0] + (m_direction[0] * m_length);
		end[1] = m_start[1] + (m_direction[1] * m_length);
		end[2] = m_start[2] + (m_direction[2] * m_length);
		
		m_PointsDiff = MathCalcsLib.calcPointsDiff(m_start, end);
		m_PointsDiffDot = MathCalcsLib.dotProduct(m_PointsDiff, m_PointsDiff);				
		
	}

	public double[] GetNormal(double[] point)  
	{ 		
		// Formulas according to http://answers.yahoo.com/question/index?qid=20080218071458AAYz1s1
		double[] AP = {0}, center = {0};
		
		// Calculate the projection of the intersection point onto the direction vector of the cylinder
		AP = MathCalcsLib.calcPointsDiff(m_start, point);
		double dot = MathCalcsLib.dotProduct(m_PointsDiff, AP) / m_PointsDiffDot;
		center = m_start.clone();
		MathCalcsLib.addVectorAndMultiply(center, m_PointsDiff, dot);

		// Calculate the vector from the intersection point to its projection onto the direction of the cylinder.
		double[] normal = MathCalcsLib.calcPointsDiff(center, point);
		MathCalcsLib.normalize(normal);
		
		return normal;
	}

	private boolean IsPointOnCylinder(double magnitude, Ray ray) {

		double[] AP = {0};
		
		ray.setMagnitude(magnitude);
		
		AP = MathCalcsLib.calcPointsDiff(m_start, ray.getEndPoint());
		double dot = MathCalcsLib.dotProduct(m_direction, AP);
		
		if(dot > m_length || dot < 0)
			return false;
		
		ray.setMagnitude(1);
		return true;
	}	
	
	public double FindIntersection(Ray ray) 
	{
		double[] AO, AOxAB, VxAB;	// Vectors to work with
		double a, b, c;		// Quadratic equation coefficients
		
		AO = MathCalcsLib.calcPointsDiff(m_start, ray.getPosition());
		AOxAB = MathCalcsLib.crossProduct(AO, m_direction);
		VxAB = MathCalcsLib.crossProduct(ray.getDirection(), m_direction);
		
		
		a = MathCalcsLib.dotProduct(VxAB, VxAB);
		b = 2 * MathCalcsLib.dotProduct(VxAB, AOxAB);
		c = MathCalcsLib.dotProduct(AOxAB, AOxAB) - (m_radius * m_radius);
		
		// Solve equation for at^2 + bt + c = 0
		double[] roots = MathCalcsLib.solveQuadraticEquation(a, b, c);
		double distance;

		if(roots[0] == Double.POSITIVE_INFINITY)
		{
			distance = Double.POSITIVE_INFINITY;
		}
		else if(roots[0] <= 0 && roots[1] <=0)
		{
			distance = Double.POSITIVE_INFINITY;
		}
		// We need to choose the closest intersection point which is within the cylinder length
		else if(roots[0] >= 0 && roots[1] >= 0)
		{
			if(IsPointOnCylinder(roots[0], ray))
			{
				if(IsPointOnCylinder(roots[1], ray)) {
					distance = Math.min(roots[0], roots[1]);
				}
				else {
					distance = roots[0];
				}
			}
			else if(IsPointOnCylinder(roots[1], ray)) {
				distance = roots[1];
			}
			else {
				distance = Double.POSITIVE_INFINITY;
			}
		}
		else {
			distance = Math.max(roots[0], roots[1]);
		}
		
		return distance;
	}
	
	public double[] GetTextureCoords(double[] point) {
		try {			
			double pointStartDiff = MathCalcsLib.norm(MathCalcsLib.calcPointsDiff(point, m_start)); 
			double dist = Math.sqrt(Math.abs(Math.pow(pointStartDiff,2) - Math.pow(m_radius,2)));
	
			Vector3D startToCenter = new Vector3D(m_start, m_direction, dist);
			double[] pointToCenter = MathCalcsLib.calcPointsDiff(point, startToCenter.getEndPoint());
			MathCalcsLib.normalize(pointToCenter);
	
			double u = dist / m_length;
			double q = MathCalcsLib.dotProduct(pointToCenter, m_referenceVector);
			if (Math.abs(q) > 1) q = 1 * Math.signum(q);
			
			double v = Math.acos(q);
			double[] orthoToPointToCenter = MathCalcsLib.crossProduct(pointToCenter, m_referenceVector);
			MathCalcsLib.normalize(orthoToPointToCenter);
			
			if (MathCalcsLib.dotProduct(orthoToPointToCenter, m_direction) < 0) {
				v = (2 * Math.PI) - v;
			}
			
			v = v / (2 * Math.PI);
			
			return new double[] { u, v };		
		}
		catch (Exception e) {
			e.printStackTrace();
			return new double[] { 0, 0 };
		}
	}		
}
