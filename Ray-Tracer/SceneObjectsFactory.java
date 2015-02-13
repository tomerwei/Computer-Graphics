
public class SceneObjectsFactory {

	public static SceneObjectsInterface buildObject(String name)
	{
		if (null == name) {
			return null;
		}
				
		if ("scene".equals(name)) return new Scene();
		if ("camera".equals(name)) return new Camera();
		if ("sphere".equals(name)) return new SceneSphere();
		if ("disc".equals(name)) return new SceneDisc();
		if ("rectangle".equals(name)) return new SceneRectangle();
		if ("cylinder".equals(name)) return new SceneCylinder();
	    if ("box".equals(name)) return new SceneBox();
		if ("light-point".equals(name)) return new LightPoint();
		if ("light-directed".equals(name)) return new LightDirected();
		
	    return null; 
	}
}
