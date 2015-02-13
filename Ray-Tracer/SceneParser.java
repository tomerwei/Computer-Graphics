
import java.util.*;

public class SceneParser extends Parser {
	
	List<SceneObjectsInterface> m_objectsList = new ArrayList<SceneObjectsInterface>();
	SceneObjectsInterface m_processedObject = null;
	Scene m_processedScene = null;	
	
	SceneParser(Scene scene)
	{
		m_processedScene = scene;
	}
		
	Scene getProcessedScene()
	{
		return m_processedScene;
	}
	
	public boolean addObject(String name) throws ParseException
	{
		if (null == name) {
			throw new ParseException("ERROR: [SceneParser::addObject] : Unkonwn Object  " + name);
		}
		
		name = name.toLowerCase();		
		if (false == name.equals("scene")) {
			m_processedObject = SceneObjectsFactory.buildObject(name);
			if (null == m_processedObject) {
				throw new ParseException("ERROR: [SceneParser::addObject] : Unkonwn Object  " + name);
			}
		}
		else {
			m_processedObject = m_processedScene;
		}
		m_processedObject.initObject();
		return true;
	}
	
	public boolean setParameter(String name, String[] args) throws Parser.ParseException
	{
		try {
			Parameter param = new Parameter(name, args); 			
			m_processedObject.setParameter(param);
		}
		catch (Exception e) 
		{			
			return false;
		}
		return true;
	}
	
	public void commit() throws ParseException
	{
		if (false == m_processedObject instanceof Scene) 
		{
			m_processedObject.Finalize();
			m_objectsList.add(m_processedObject);			
		}
	}
	
	public void endFile() throws ParseException
	{
		try {
			// todo - post process
			for (int i=0; i<m_objectsList.size(); i++) {
				m_processedScene.addObject(m_objectsList.get(i));
			}
		}
		catch (Exception e) {
			reportError("[SceneParser::endFile] " + e.getMessage());
			return;			
		}
	}	
	
	//converts vector of strings to vector of doubles
	public static double[] StringArrToDoubleArr(String[] stringArr) throws Exception 
	{
		double[] doubleArr = new double[stringArr.length];		 
				
		for (int i = 0; i < stringArr.length; i++) 
			doubleArr[i] = Double.parseDouble(stringArr[i]);
		
		return doubleArr;
	}			
}
