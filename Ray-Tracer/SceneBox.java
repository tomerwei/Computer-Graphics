import java.util.HashMap;
import java.util.Map;

public class SceneBox extends SceneGeometricPrimitive implements SceneObjectsInterface {
	
	enum tParts { P0, P1, P2, P3, UNKNOWN };	
	Map<String, tParts> stringToPart;
	
	double[] m_p0={0}, m_p1={0}, m_p2={0}, m_p3={0};
	private SceneRectangle[] m_rects;
	
	SceneRectangle m_processedIntersectingRect = null;
	int m_processedIntersectingRectIdx = -1;
	
	public void initObject() 
	{		
		super.initObject();
		
		stringToPart = new HashMap<String, tParts>();
		stringToPart.put("p0", tParts.P0);
		stringToPart.put("p1", tParts.P1);
		stringToPart.put("p2", tParts.P2);		
		stringToPart.put("p3", tParts.P3);		
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
			case P3:
				m_p3 = SceneParser.StringArrToDoubleArr(param.getValueArray());
				break;
			default:
				System.out.println("Error [SceneBox::setParameter] : Unkonwn Part [" + param.getName() + "]");
				return false;
		}		
		return true;
	}
	
	public void Finalize()
	{
		super.Finalize();
		
		double[] p0_p1 = MathCalcsLib.calcPointsDiff(m_p0, m_p1);
		double[] p0_p3 = MathCalcsLib.calcPointsDiff(m_p0, m_p3);
		
		m_rects = new SceneRectangle[6];
		m_rects[0] = new SceneRectangle(m_p0, m_p1, m_p2);
		m_rects[1] = new SceneRectangle(m_p0, m_p2, m_p3);
		m_rects[2] = new SceneRectangle(m_p0, m_p3, m_p1);
		
		m_rects[3] = new SceneRectangle(m_p1, MathCalcsLib.addPoints(m_p3, p0_p1), MathCalcsLib.addPoints(m_p2, p0_p1));
		m_rects[4] = new SceneRectangle(m_p2, MathCalcsLib.addPoints(m_p2, p0_p1), MathCalcsLib.addPoints(m_p2, p0_p3));
		m_rects[5] = new SceneRectangle(m_p3, MathCalcsLib.addPoints(m_p2, p0_p3), MathCalcsLib.addPoints(m_p3, p0_p1));
	}
	
	public double[] GetNormal(double[] point)  
	{	
		if (null != m_processedIntersectingRect) {
			return m_processedIntersectingRect.GetNormal(point);
		}
		return null;
	}
	
	public double FindIntersection(Ray ray) 
	{	
		double minDistance = Double.POSITIVE_INFINITY;
		double currentDistance = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < m_rects.length; i++)
		{
			currentDistance = m_rects[i].FindIntersection(ray);

			if (currentDistance < minDistance)
			{
				minDistance = currentDistance;
				m_processedIntersectingRect = m_rects[i];
				m_processedIntersectingRectIdx = i;
			}
		}

		return minDistance;
	}
	
	public double[] GetTextureCoords(double[] point) 
	{
		if (null != m_processedIntersectingRect) {
			return m_processedIntersectingRect.GetTextureCoords(point);
		}
		return null;
	}	
}
