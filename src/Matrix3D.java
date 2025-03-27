public class Matrix3D {
    private double[] values;
    public Matrix3D(double[] values){
        this.values = values;
    }

    //Multiplying two 3x3 matrix together
    public Matrix3D multiply(Matrix3D otherMatrix){
        double[] results = new double[9];
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < 3; col++){
                for(int iterator = 0; iterator < 3; iterator++){
                    results[row*3 + col] += this.values[row*3+iterator] * otherMatrix.values[iterator*3 + col];
                }
            }
        }
        return new Matrix3D(results);
    }
    //multiply existing vertex with math to rotate
    public Vertex transform(Vertex in){
        return new Vertex(
            in.x * values[0] + in.y * values[3] + in.z*values[6],
            in.x * values[1] + in.y * values[4] + in.z*values[7],
            in.x * values[2] + in.y * values[5] + in.z*values[8]
            
        );
    }
}
