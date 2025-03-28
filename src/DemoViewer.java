import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
public class DemoViewer {
    public static void main(String[] args){
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        
        //slider to control  horizontal rotation
        JSlider headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);


        //slider for virtical rotation
        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        

        //panel to display render result
        //this is called an anonymous inner class that extends JPanel and overides paintComponent
        JPanel renderPanel = new JPanel(){
            public void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // magic here
                List<Triangle> tris = new ArrayList<Triangle>();
                tris.add(new Triangle(new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(-100, 100, -100),
                        Color.WHITE));
                tris.add(new Triangle(new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(100, -100, -100),
                        Color.RED));
                tris.add(new Triangle(new Vertex(-100, 100, -100),
                        new Vertex(100, -100, -100),
                        new Vertex(100, 100, 100),
                        Color.GREEN));
                tris.add(new Triangle(new Vertex(-100, 100, -100),
                        new Vertex(100, -100, -100),
                        new Vertex(-100, -100, 100),
                        Color.BLUE));

                double heading = Math.toRadians(headingSlider.getValue());
                Matrix3D transformXZ = new Matrix3D(new double[]{
                    Math.cos(heading), 0, -Math.sin(heading),
                    0, 1, 0,
                    Math.sin(heading), 0, Math.cos(heading)
                });    
                double pitch = Math.toRadians(pitchSlider.getValue());
                Matrix3D transformYZ = new Matrix3D(new double[]{
                    1, 0 ,0,
                    0, Math.cos(pitch), Math.sin(pitch),
                    0, -Math.sin(pitch), Math.cos(pitch)
                });
                
                Matrix3D transformBoth = transformXZ.multiply(transformYZ);
                // g2.translate(getWidth()/2, getHeight()/2);
                // g2.setColor(Color.WHITE);
                // for(Triangle t : tris){
                //     Vertex v1 = transformBoth.transform(t.v1);
                //     Vertex v2 = transformBoth.transform(t.v2);
                //     Vertex v3 = transformBoth.transform(t.v3);
                //     Path2D path = new Path2D.Double();
                //     path.moveTo(v1.x, v1.y);
                //     path.lineTo(v3.x, v3.y);
                //     path.lineTo(v2.x, v2.y);
                //     path.closePath();
                //     g2.draw(path);
                //}
                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                //setting up a z buffer value for each pixel in the window
                double[] zBuffer = new double[img.getWidth()*img.getHeight()];
                
                //pre setting each pixel z value to be negative inf 
                //as we dont know which order to draw them yet
                for(int q = 0; q < zBuffer.length; q++){
                    zBuffer[q] = Double.NEGATIVE_INFINITY;
                }

                for (Triangle t : tris){
                    Vertex v1 = transformBoth.transform(t.v1);
                    Vertex v2 = transformBoth.transform(t.v2);
                    Vertex v3 = transformBoth.transform(t.v3);
                    

                    //manual translation
                    v1.x += getWidth() / 2;
                    v1.y += getHeight() / 2;
                    v2.x += getWidth() / 2;
                    v2.y += getHeight() / 2;
                    v3.x += getWidth() / 2;
                    v3.y += getHeight() / 2;


                    //calculate the edge vector of each triangle
                    //v1=a, v2=b, v3=c
                    Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);

                    //calculate normal vector (vector that is perpendicular to the plane)
                    Vertex normalVector = new Vertex(ab.y*ac.z - ab.z*ac.y, 
                                                    ab.z*ac.x - ab.x*ac.z,
                                                    ab.x*ac.y - ab.y*ac.x);

                    //calculate length of normal vector
                    double normalLength = Math.sqrt(normalVector.x * normalVector.x + normalVector.y * normalVector.y + normalVector.z * normalVector.z);

                    //now we normalize the vector aka make the length = 1 for light calculation
                    normalVector.x /= normalLength;
                    normalVector.y /= normalLength;
                    normalVector.z /= normalLength;

                    //now we calculate the cos between the normal vector and the light
                    //for simplicity we place our light at [0, 0, 1]
                    //the length of light and our normal vector is both 1 so we can simplify our formula
                    double cosAngle = Math.abs(normalVector.z);
                    //get the color of dimly lit triangle
                    Color shadeColor = getShade(t.color, cosAngle);
                    

                    //get a box of the position of the triangle to optimise fill
                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(getWidth()-1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(getHeight()-1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));
                    
                    //barycentric coords
                    //b1 = Area(P V1 V2)/ Area(V1,V2,V3)
                    //b2 = Area(P V2 V3)/ Area(V1,V2,V3)
                    //b3 = Area(P V3 V1)/ Area(V1,V2,V3)

                    //area(ABC) = 1/2 vectAB * vectAC
                    //since using the same formula so just remove the 1/2 and the -
                    double triangleArea = (v1.y-v3.y)*(v2.x-v3.x) + (v3.x-v1.x)*(v2.y-v3.y);
                    for(int y = minY; y <= maxY; y++){
                        for(int x = minX; x <= maxX; x++){
                            double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                            double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                            double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;

                            if(b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1){
                                //using the barycentric coords formula to find P's z coords
                                double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                                //creating an index to know which pixel we are currently selecting
                                int zIndex = y * img.getWidth() + x;

                                
                                if(zBuffer[zIndex] < depth){
                                img.setRGB(x, y, shadeColor.getRGB());
                                zBuffer[zIndex] = depth;
                                }
                            }
                        }
                    }

                }
                g2.drawImage(img, 0, 0, null);
                
            }
        };
        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());

        pane.add(renderPanel, BorderLayout.CENTER);
        
        frame.setSize(400, 400);
        frame.setVisible(true);
    }
    //Convert the color shade base on the angle
    public static Color getShade(Color color, double shade){
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);
        return new Color(red, green, blue);
    }
}
