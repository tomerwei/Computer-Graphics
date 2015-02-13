//package JautOGL;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;
//import java.applet.*;
//import java.awt.*;
//import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
//import com.sun.opengl.util.*; // GLUT, FPSAnimator

import com.jogamp.common.nio.Buffers;

// import net.java.games.jogl.*;
// import net.java.games.jogl.util.*;

public class GLModel {

	private ArrayList vertexsets;
	private ArrayList vertexsetsnorms;
	private ArrayList vertexsetstexs;
	private ArrayList faces;
	private ArrayList facestexs;
	private ArrayList facesnorms;
	private ArrayList mattimings;
	private MtlLoader materials;
	private int objectlist;
	private int numpolys;
	public float toppoint;
	public float bottompoint;
	public float leftpoint;
	public float rightpoint;
	public float farpoint;
	public float nearpoint;
	private String mtl_path;

	public GLModel(BufferedReader ref, boolean centerit, String path, GL2 gl, float nScaleDownRatio) {

		mtl_path = path;
		vertexsets = new ArrayList();
		vertexsetsnorms = new ArrayList();
		vertexsetstexs = new ArrayList();
		faces = new ArrayList();
		facestexs = new ArrayList();
		facesnorms = new ArrayList();
		mattimings = new ArrayList();
		numpolys = 0;
		toppoint = 0.0F;
		bottompoint = 0.0F;
		leftpoint = 0.0F;
		rightpoint = 0.0F;
		farpoint = 0.0F;
		nearpoint = 0.0F;
		loadobject(ref);
		if (centerit) {
			centerit();
		}
		scaleDown(nScaleDownRatio);
		openglDrawDisplayList(gl); // Initializes the display list
		numpolys = faces.size();
		// cleanup();
	}

	private void cleanup() {
		vertexsets.clear();
		vertexsetsnorms.clear();
		vertexsetstexs.clear();
		faces.clear();
		facestexs.clear();
		facesnorms.clear();
	}

	private void loadobject(BufferedReader br) {
		int linecounter = 0;
		int facecounter = 0;
		try {
			boolean firstpass = true;
			String newline;
			while ((newline = br.readLine()) != null) {
				linecounter++;
				if (newline.length() > 0) {
					newline = newline.trim();

					// LOADS VERTEX COORDINATES
					if (newline.startsWith("v ")) {
						float coords[] = new float[4];
						String coordstext[] = new String[4];
						newline = newline.substring(2, newline.length());
						StringTokenizer st = new StringTokenizer(newline, " ");
						for (int i = 0; st.hasMoreTokens(); i++)
							coords[i] = Float.parseFloat(st.nextToken());

						if (firstpass) {
							rightpoint = coords[0];
							leftpoint = coords[0];
							toppoint = coords[1];
							bottompoint = coords[1];
							nearpoint = coords[2];
							farpoint = coords[2];
							firstpass = false;
						}
						if (coords[0] > rightpoint)
							rightpoint = coords[0];
						if (coords[0] < leftpoint)
							leftpoint = coords[0];
						if (coords[1] > toppoint)
							toppoint = coords[1];
						if (coords[1] < bottompoint)
							bottompoint = coords[1];
						if (coords[2] > nearpoint)
							nearpoint = coords[2];
						if (coords[2] < farpoint)
							farpoint = coords[2];
						vertexsets.add(coords);
					} else

					// LOADS VERTEX TEXTURE COORDINATES
					if (newline.startsWith("vt")) {
						float coords[] = new float[4];
						String coordstext[] = new String[4];
						newline = newline.substring(3, newline.length());
						StringTokenizer st = new StringTokenizer(newline, " ");
						for (int i = 0; st.hasMoreTokens(); i++)
							coords[i] = Float.parseFloat(st.nextToken());

						vertexsetstexs.add(coords);
					} else

					// LOADS VERTEX NORMALS COORDINATES
					if (newline.startsWith("vn")) {
						float coords[] = new float[4];
						String coordstext[] = new String[4];
						newline = newline.substring(3, newline.length());
						StringTokenizer st = new StringTokenizer(newline, " ");
						for (int i = 0; st.hasMoreTokens(); i++)
							coords[i] = Float.parseFloat(st.nextToken());

						vertexsetsnorms.add(coords);
					} else

					// LOADS FACES COORDINATES
					if (newline.startsWith("f ")) {
						facecounter++;
						newline = newline.substring(2, newline.length());
						StringTokenizer st = new StringTokenizer(newline, " ");
						int count = st.countTokens();
						int v[] = new int[count];
						int vt[] = new int[count];
						int vn[] = new int[count];
						for (int i = 0; i < count; i++) {
							char chars[] = st.nextToken().toCharArray();
							StringBuffer sb = new StringBuffer();
							char lc = 'x';
							for (int k = 0; k < chars.length; k++) {
								if (chars[k] == '/' && lc == '/')
									sb.append('0');
								lc = chars[k];
								sb.append(lc);
							}

							StringTokenizer st2 = new StringTokenizer(
									sb.toString(), "/");
							int num = st2.countTokens();
							v[i] = Integer.parseInt(st2.nextToken());
							if (num > 1)
								vt[i] = Integer.parseInt(st2.nextToken());
							else
								vt[i] = 0;
							if (num > 2)
								vn[i] = Integer.parseInt(st2.nextToken());
							else
								vn[i] = 0;
						}

						faces.add(v);
						facestexs.add(vt);
						facesnorms.add(vn);
					} else

					// LOADS MATERIALS
					if (newline.charAt(0) == 'm' && newline.charAt(1) == 't'
							&& newline.charAt(2) == 'l'
							&& newline.charAt(3) == 'l'
							&& newline.charAt(4) == 'i'
							&& newline.charAt(5) == 'b') {
						String[] coordstext = new String[3];
						coordstext = newline.split("\\s+");
						if (mtl_path != null)
							loadmaterials();
					} else

					// USES MATELIALS
					if (newline.charAt(0) == 'u' && newline.charAt(1) == 's'
							&& newline.charAt(2) == 'e'
							&& newline.charAt(3) == 'm'
							&& newline.charAt(4) == 't'
							&& newline.charAt(5) == 'l') {
						String[] coords = new String[2];
						String[] coordstext = new String[3];
						coordstext = newline.split("\\s+");
						coords[0] = coordstext[1];
						coords[1] = facecounter + "";
						mattimings.add(coords);
						// System.out.println(coords[0] + ", " + coords[1]);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Failed to read file: " + br.toString());
		} catch (NumberFormatException e) {
			System.out.println("Malformed OBJ file: " + br.toString() + "\r \r"
					+ e.getMessage());
		}
	}

	private void loadmaterials() {
		FileReader frm;
		String refm = mtl_path;

		try {
			frm = new FileReader(refm);
			BufferedReader brm = new BufferedReader(frm);
			materials = new MtlLoader(brm, mtl_path);
			frm.close();
		} catch (IOException e) {
			System.out.println("Could not open file: " + refm);
			materials = null;
		}
	}

	private void scaleDown(float scaleDownRatio) {
		float delta = this.getXWidth();
		float deltax = (rightpoint - leftpoint);
		float deltay = (toppoint - bottompoint);
		float deltaz = (nearpoint - farpoint);
		delta = deltax;
		if (delta < deltay) {
			delta = deltay;
		}
		if (delta < deltaz) {
			delta = deltaz;
		}
		float multiplier = (float) (delta * 0.5);
		multiplier = (float) scaleDownRatio;

		for (int i = 0; i < vertexsets.size(); i++) {
			float coords[] = new float[4];
			coords[0] = ((float[]) vertexsets.get(i))[0] * (float) multiplier;// multiplier;
			coords[1] = ((float[]) vertexsets.get(i))[1] * (float) multiplier;// multiplier;
			coords[2] = ((float[]) vertexsets.get(i))[2] * (float) multiplier;// multiplier;			
			vertexsets.set(i, coords);
		}
	}

	private void centerit() {
		float xshift = (rightpoint - leftpoint) / 2.0F;
		float yshift = (toppoint - bottompoint) / 2.0F;
		float zshift = (nearpoint - farpoint) / 2.0F;
		for (int i = 0; i < vertexsets.size(); i++) {
			float coords[] = new float[4];
			coords[0] = ((float[]) vertexsets.get(i))[0] - leftpoint - xshift;
			coords[1] = ((float[]) vertexsets.get(i))[1] - bottompoint - yshift;
			coords[2] = ((float[]) vertexsets.get(i))[2] - farpoint - zshift ;
			vertexsets.set(i, coords);
		}

	}

	public void setXWidth() {
		rightpoint = 5.0F;
		leftpoint = 10.0F;
	}

	public float getXWidth() {
		float returnval = 0.0F;
		returnval = rightpoint - leftpoint;
		return returnval;
	}

	public float getYHeight() {
		float returnval = 0.0F;
		returnval = toppoint - bottompoint;
		return returnval;
	}

	public float getZDepth() {
		float returnval = 0.0F;
		returnval = nearpoint - farpoint;
		return returnval;
	}

	public int numpolygons() {
		return numpolys;
	}

	public void openglVertrexArray(GL2 gl) {

		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

		int nextmat = -1;
		int matcount = 0;
		int totalmats = mattimings.size();
		String[] nextmatnamearray = null;
		String nextmatname = null;

		if (totalmats > 0 && materials != null) {
			nextmatnamearray = (String[]) (mattimings.get(matcount));
			nextmatname = nextmatnamearray[0];
			nextmat = Integer.parseInt(nextmatnamearray[1]);
		}

		for (int i = 0; i < faces.size(); i++) {
			if (i == nextmat) {
				gl.glEnable(GL2.GL_COLOR_MATERIAL);
				// l.glColor4f((materials.getKd(nextmatname))[0],(materials.getKd(nextmatname))[1],(materials.getKd(nextmatname))[2],(materials.getd(nextmatname)));
				matcount++;
				if (matcount < totalmats) {
					nextmatnamearray = (String[]) (mattimings.get(matcount));
					nextmatname = nextmatnamearray[0];
					nextmat = Integer.parseInt(nextmatnamearray[1]);
				}
			}

			int[] tempfaces = (int[]) (faces.get(i));
			int[] tempfacesnorms = (int[]) (facesnorms.get(i));
			int[] tempfacestexs = (int[]) (facestexs.get(i));

			// // Quad Begin Header ////
			int polytype;
			if (tempfaces.length == 3) {
				polytype = gl.GL_TRIANGLES;
			} else if (tempfaces.length == 4) {
				polytype = gl.GL_QUADS;
			} else {
				polytype = gl.GL_POLYGON;
			}

			float[] vertices = new float[tempfaces.length * 3];
			float[] norms = new float[tempfaces.length * 3];
			float[] textures = new float[tempfaces.length * 3];
			for (int w = 0; w < tempfaces.length; w++) {
				if (tempfacesnorms[w] != 0) {
					norms[(3 * w)] = ((float[]) vertexsetsnorms
							.get(tempfacesnorms[w] - 1))[0];
					norms[(3 * w) + 1] = ((float[]) vertexsetsnorms
							.get(tempfacesnorms[w] - 1))[1];
					norms[(3 * w) + 2] = ((float[]) vertexsetsnorms
							.get(tempfacesnorms[w] - 1))[2];
				} else {
					norms[(3 * w)] = 0f;
					norms[(3 * w) + 1] = 0f;
					norms[(3 * w) + 2] = 0f;
				}

				if (tempfacestexs[w] != 0) {
					textures[(3 * w)] = ((float[]) vertexsetstexs
							.get(tempfacestexs[w] - 1))[0];
					textures[(3 * w) + 1] = ((float[]) vertexsetstexs
							.get(tempfacestexs[w] - 1))[1];
					textures[(3 * w) + 2] = ((float[]) vertexsetstexs
							.get(tempfacestexs[w] - 1))[2];
				} else {
					textures[(3 * w)] = 0f;
					textures[(3 * w) + 1] = 0f;
					textures[(3 * w) + 2] = 0f;
				}

				vertices[(3 * w)] = ((float[]) vertexsets.get(tempfaces[w] - 1))[0];
				vertices[(3 * w) + 1] = ((float[]) vertexsets
						.get(tempfaces[w] - 1))[1];
				vertices[(3 * w) + 2] = ((float[]) vertexsets
						.get(tempfaces[w] - 1))[2];
			}

			FloatBuffer tmpVerticesBuf = Buffers
					.newDirectFloatBuffer(vertices.length);
			for (int j = 0; j < vertices.length; j++) {
				tmpVerticesBuf.put(vertices[j]);
			}
			tmpVerticesBuf.rewind();

			FloatBuffer tmpNormsBuf = Buffers
					.newDirectFloatBuffer(norms.length);
			for (int j = 0; j < norms.length; j++) {
				tmpNormsBuf.put(norms[j]);
			}
			tmpNormsBuf.rewind();

			FloatBuffer tmpTexBuf = Buffers
					.newDirectFloatBuffer(textures.length);
			for (int j = 0; j < textures.length; j++) {
				tmpTexBuf.put(textures[j]);
			}
			tmpTexBuf.rewind();

			gl.glTexCoordPointer(3, GL2.GL_FLOAT, 0, tmpTexBuf);
			gl.glNormalPointer(GL2.GL_FLOAT, 0, tmpNormsBuf);
			gl.glVertexPointer(3, GL2.GL_FLOAT, 0, tmpVerticesBuf);
			gl.glDrawArrays(polytype, 0, tempfaces.length);
		}
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	}

	public void openglDrawBruteForce(GL2 gl) {
		int nextmat = -1;
		int matcount = 0;
		int totalmats = mattimings.size();
		String[] nextmatnamearray = null;
		String nextmatname = null;

		if (totalmats > 0 && materials != null) {
			nextmatnamearray = (String[]) (mattimings.get(matcount));
			nextmatname = nextmatnamearray[0];
			nextmat = Integer.parseInt(nextmatnamearray[1]);
		}
		for (int i = 0; i < faces.size(); i++) {
			if (i == nextmat) {
				gl.glEnable(GL2.GL_COLOR_MATERIAL);
				// l.glColor4f((materials.getKd(nextmatname))[0],(materials.getKd(nextmatname))[1],(materials.getKd(nextmatname))[2],(materials.getd(nextmatname)));
				matcount++;
				if (matcount < totalmats) {
					nextmatnamearray = (String[]) (mattimings.get(matcount));
					nextmatname = nextmatnamearray[0];
					nextmat = Integer.parseInt(nextmatnamearray[1]);
				}
			}

			int[] tempfaces = (int[]) (faces.get(i));
			int[] tempfacesnorms = (int[]) (facesnorms.get(i));
			int[] tempfacestexs = (int[]) (facestexs.get(i));

			// // Quad Begin Header ////
			int polytype;
			if (tempfaces.length == 3) {
				polytype = gl.GL_TRIANGLES;
			} else if (tempfaces.length == 4) {
				polytype = gl.GL_QUADS;
			} else {
				polytype = gl.GL_POLYGON;
			}
			gl.glBegin(polytype);
			// //////////////////////////

			for (int w = 0; w < tempfaces.length; w++) {
				if (tempfacesnorms[w] != 0) {
					float normtempx = ((float[]) vertexsetsnorms
							.get(tempfacesnorms[w] - 1))[0];
					float normtempy = ((float[]) vertexsetsnorms
							.get(tempfacesnorms[w] - 1))[1];
					float normtempz = ((float[]) vertexsetsnorms
							.get(tempfacesnorms[w] - 1))[2];
					gl.glNormal3f(normtempx, normtempy, normtempz);
				}

				if (tempfacestexs[w] != 0) {
					float textempx = ((float[]) vertexsetstexs
							.get(tempfacestexs[w] - 1))[0];
					float textempy = ((float[]) vertexsetstexs
							.get(tempfacestexs[w] - 1))[1];
					float textempz = ((float[]) vertexsetstexs
							.get(tempfacestexs[w] - 1))[2];
					gl.glTexCoord3f(textempx, 1f - textempy, textempz);
				}

				float tempx = ((float[]) vertexsets.get(tempfaces[w] - 1))[0];
				float tempy = ((float[]) vertexsets.get(tempfaces[w] - 1))[1];
				float tempz = ((float[]) vertexsets.get(tempfaces[w] - 1))[2];
				gl.glVertex3f(tempx, tempy, tempz);
			}

			gl.glEnd();

		}
	}

	public void openglDrawDisplayList(GL2 gl) {
		this.objectlist = gl.glGenLists(1);
		int nextmat = -1;
		int matcount = 0;
		int totalmats = mattimings.size();
		String[] nextmatnamearray = null;
		String nextmatname = null;

		if (totalmats > 0 && materials != null) {
			nextmatnamearray = (String[]) (mattimings.get(matcount));
			nextmatname = nextmatnamearray[0];
			nextmat = Integer.parseInt(nextmatnamearray[1]);
		}

		gl.glNewList(objectlist, 4864);
		for (int i = 0; i < faces.size(); i++) {
			if (i == nextmat) {
				gl.glEnable(GL2.GL_COLOR_MATERIAL);
				// l.glColor4f((materials.getKd(nextmatname))[0],(materials.getKd(nextmatname))[1],(materials.getKd(nextmatname))[2],(materials.getd(nextmatname)));
				matcount++;
				if (matcount < totalmats) {
					nextmatnamearray = (String[]) (mattimings.get(matcount));
					nextmatname = nextmatnamearray[0];
					nextmat = Integer.parseInt(nextmatnamearray[1]);
				}
			}

			int[] tempfaces = (int[]) (faces.get(i));
			int[] tempfacesnorms = (int[]) (facesnorms.get(i));
			int[] tempfacestexs = (int[]) (facestexs.get(i));

			// // Quad Begin Header ////
			int polytype;
			if (tempfaces.length == 3) {
				polytype = gl.GL_TRIANGLES;
			} else if (tempfaces.length == 4) {
				polytype = gl.GL_QUADS;
			} else {
				polytype = gl.GL_POLYGON;
			}
			gl.glBegin(polytype);
			// //////////////////////////

			for (int w = 0; w < tempfaces.length; w++) {
				if (tempfacesnorms[w] != 0) {
					float normtempx = ((float[]) vertexsetsnorms
							.get(tempfacesnorms[w] - 1))[0];
					float normtempy = ((float[]) vertexsetsnorms
							.get(tempfacesnorms[w] - 1))[1];
					float normtempz = ((float[]) vertexsetsnorms
							.get(tempfacesnorms[w] - 1))[2];
					gl.glNormal3f(normtempx, normtempy, normtempz);
				}

				if (tempfacestexs[w] != 0) {
					float textempx = ((float[]) vertexsetstexs
							.get(tempfacestexs[w] - 1))[0];
					float textempy = ((float[]) vertexsetstexs
							.get(tempfacestexs[w] - 1))[1];
					float textempz = ((float[]) vertexsetstexs
							.get(tempfacestexs[w] - 1))[2];
					gl.glTexCoord3f(textempx, 1f - textempy, textempz);
				}

				float tempx = ((float[]) vertexsets.get(tempfaces[w] - 1))[0];
				float tempy = ((float[]) vertexsets.get(tempfaces[w] - 1))[1];
				float tempz = ((float[]) vertexsets.get(tempfaces[w] - 1))[2];
				gl.glVertex3f(tempx, tempy, tempz);
			}
			gl.glEnd();
		}
		gl.glEndList();
	}

	public void opengldraw(GL2 gl, int state) {
		if (0 == state % 3) {
			// using display lists
			gl.glCallList(objectlist);
		} else if (1 == state % 3) {
			// using brute force
			openglDrawBruteForce(gl);

		} else if (2 == state % 3) {
			// using vertrex arrays
			openglVertrexArray(gl);

		}
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
	}
}
