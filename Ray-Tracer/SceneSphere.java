import java.util.HashMap;
import java.util.Map;

public class SceneSphere extends SceneGeometricPrimitive implements SceneObjectsInterface {

	enum tParts { CENTER, RADIUS, UNKNOWN };	
	Map<String, tParts> stringToPart;
	
	double[] m_center = null;
	double m_radius = 0;
	
	public void initObject() 
	{
		super.initObject();
		
		stringToPart = new HashMap<String, tParts>();
		stringToPart.put("center", tParts.CENTER);
		stringToPart.put("radius", tParts.RADIUS);
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
			default:
				System.out.println("Error [SceneSphere::setParameter] : Unkonwn Part [" + param.getName() + "]");
				return false;
		}		
		return true;
	}

	public void Finalize()
	{
		super.Finalize();
	}
	
	public double[] GetNormal(double[] point)  
	{								
		double[] normal = MathCalcsLib.calcPointsDiff(m_center, point);		
		MathCalcsLib.normalize(normal);
		return normal;
	}
	
	public double FindIntersection(Ray ray) 
	{
		double[] L = MathCalcsLib.calcPointsDiff(ray.getPosition(), m_center);
		double[] V = ray.getDirection();
		
		double tCA = MathCalcsLib.dotProduct(L, V);
		
		if(tCA < 0) {
			return Double.POSITIVE_INFINITY;
		}
		
		double LSquare = MathCalcsLib.dotProduct(L, L);
		
		double dSquare =  LSquare - MathCalcsLib.sqr(tCA);
		double radiusSquare = MathCalcsLib.sqr(m_radius);

		if(dSquare > radiusSquare) {
			// In this case the ray misses the sphere
			return Double.POSITIVE_INFINITY;
		}
		
		double tHC = Math.sqrt(radiusSquare - dSquare);
		if(MathCalcsLib.dotProduct(L, L) < LSquare) {
			return tCA + tHC;
		}
		else {
			return tCA - tHC;
		}
	}
	
	public double[] GetTextureCoords(double[] point) 
	{
		double[] rp = MathCalcsLib.calcPointsDiff(m_center, point);
		
        double v = rp[2] / m_radius;
        
        if (Math.abs(v) > 1) v -= 1 * Math.signum(v);
        v = Math.acos(v);
        
        double u = rp[0] / (m_radius * Math.sin(v));
        
        if (Math.abs(u) > 1) u = Math.signum(u);
        u = Math.acos(u);               
        
        if (rp[1] < 0)
            u = -u;
        if (rp[2] < 0)
            v = v + Math.PI;
        
        if (Double.isNaN(u)) {
        	int a = 0; a++;
        }
        
        u = (u / (2 * Math.PI));
        v = (v / Math.PI);
        
        if (u > 1) u -= 1;
        if (u < 0) u += 1;
        
        if (v > 1) v -= 1;
        if (v < 0) v += 1;
        
        return new double[] {u , v };						
	}	
}
