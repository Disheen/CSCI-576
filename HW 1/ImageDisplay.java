
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class ImageDisplay {

	JFrame frame;
	JFrame frame2;
	JFrame frame3;
	JLabel lbIm1;
	JLabel lbIm2;
	JLabel lbIm3;
	BufferedImage imgOne;
	int width = 1920; 
	int height = 1080;
	BufferedImage processedImage;
	BufferedImage scaledImage;


	class YUV_matrix {
		double y, u, v;

		public YUV_matrix(double y, double u, double v) {
			this.y = y;
			this.u = u;
			this.v = v;
		}
	}

	class RGB_matrix {
		int r, g, b;

		public RGB_matrix(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}

	public YUV_matrix[][] readImageRGB(int width, int height, String imgPath, BufferedImage img, YUV_matrix[][] matrixYUV) {
		try {
			int frameLength = width * height * 3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int r = bytes[ind];
					int g = bytes[ind + height * width];
					int b = bytes[ind + height * width * 2];

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					img.setRGB(x, y, pix);
					r = r & 0xff;
					g = g & 0xFF;
					b = b & 0xFF;

					
					double[] arrYUV = convertRBGtoYUV(r, g, b); 
					YUV_matrix objYUV = new YUV_matrix(arrYUV[0], arrYUV[1], arrYUV[2]);
					matrixYUV[y][x] = objYUV;

					ind++;
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matrixYUV;
	}


	public void showIms(String[] args) {

		String param1 = args[1];
		int input_Y = Integer.parseInt(args[1]);
		int input_U = Integer.parseInt(args[2]);
		int input_V = Integer.parseInt(args[3]);
		processedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		System.out.println("The second parameter was: " + param1);
		
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		YUV_matrix[][] matrixYUV = new YUV_matrix[height][width];
		matrixYUV=readImageRGB(width, height, args[0], imgOne, matrixYUV);
		matrixYUV=upSampling(width, height, input_Y, input_U, input_V, matrixYUV);
		System.out.println(matrixYUV);
		
		float scale_x=Float.parseFloat(args[4]);
		float scale_y=Float.parseFloat(args[5]);

		int newWidth=Math.round(width*scale_x);
		int newHeight=Math.round(height*scale_y);
		int A = Integer.parseInt(args[6]);
		displayConvertedImage(width,height,matrixYUV);
		if(A==1){
				processedImage= antiAliasing(processedImage,width,height);
		}
		scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		
		scaleImage(processedImage,scaledImage, width, height,newWidth,newHeight,scale_x, scale_y);
		

		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(imgOne));
		lbIm1.setText("Orignal Image");
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		frame.pack();
		frame.setVisible(true);


		
		frame3 = new JFrame();
		frame3.getContentPane().setLayout(gLayout);

		lbIm3 = new JLabel(new ImageIcon(scaledImage));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame3.getContentPane().add(lbIm3, c);

		frame3.pack();
		frame3.setVisible(true);
	
	}


	public void displayConvertedImage(int width, int height,  YUV_matrix[][] matrixYUV) {

		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
	
				YUV_matrix yuv = matrixYUV[i][j];
				
				int[] arrRGB = convertYUVtoRGB(yuv.y, yuv.u, yuv.v);
				int R = arrRGB[0];
				int G = arrRGB[1];
				int B = arrRGB[2];	
				R=capVal(R);
				G=capVal(G);
				B=capVal(B);
	
							
				int processedPixel = 0xff000000 | ((R) << 16) | ((G) << 8) | (B);
				processedImage.setRGB(j, i, processedPixel);
				
				
			}
		}
	}
	
	public YUV_matrix[][] upSampling(int width, int height,int input_Y,int input_U, int input_V, YUV_matrix[][] matrixYUV) {
	
	for(int i = 0; i < height; i++) {
		for(int j = 0; j < width; j++) {
	
			if(input_Y !=0 && input_U != 0 && input_V != 0){
				matrixYUV = upSample(matrixYUV, input_Y, width, i, j, "Y");
				matrixYUV = upSample(matrixYUV, input_U, width, i, j, "U");
				matrixYUV = upSample(matrixYUV, input_V, width, i, j, "V");
			}
	
		}
	
	}
	return matrixYUV;
	}
	



	public double[] convertRBGtoYUV(int R, int G, int B) {
		double[] YUV_matrix = new double[3];

		YUV_matrix[0] = (0.299 * R + 0.587 * G + 0.114 * B);
		YUV_matrix[1] = (0.596 * R + (-0.274 * G) + (-0.322 * B));
		YUV_matrix[2] = (0.211 * R + (-0.523 * G) + (0.312 * B));

		return YUV_matrix;
	}

	
	public int[] convertYUVtoRGB(double Y, double U, double V) {
		int[] RGB_matrix = new int[3];

		RGB_matrix[0] = (int) (1.000 * Y + 0.956 * U + 0.621 * V);
		RGB_matrix[1] = (int) (1.000 * Y + (-0.272 * U) + (-0.647 * V));
		RGB_matrix[2] = (int) (1.000 * Y + (-1.106 * U) + (1.703 * V));

		return RGB_matrix;
	}
;	public int capVal(int P) {
		if (P>255){
			return 255;
		}	
		if(P<0){
			return 0;
		}
		return P;
	}

	public YUV_matrix[][] upSample(YUV_matrix[][] matrixYUV, int gap, int width, int i, int j, String sample) {

		int k = j % gap;

		if(k != 0) {
			int prev = j-k;
			int next = j+gap-k; 

			if(next < width) {
				YUV_matrix prevYUV = matrixYUV[i][prev];
				YUV_matrix currentYUV = matrixYUV[i][j];
				YUV_matrix nextYUV = matrixYUV[i][next];
				
				if(sample == "Y") {
					currentYUV.y = (prevYUV.y + nextYUV.y)/2;
				}else if(sample == "U") {
					currentYUV.u = ((gap - k)* prevYUV.u + (k * nextYUV.u))/gap;
				}else if(sample == "V") {
					currentYUV.v = ((gap - k)* prevYUV.v + (k * nextYUV.v))/gap;
				}				
			} else {
				YUV_matrix prevYUV = matrixYUV[i][prev];

				for(int m = prev+1; m < width; m++) {
					YUV_matrix currentYUV = matrixYUV[i][m];
					if(sample == "Y") {
						currentYUV.y = prevYUV.y;
					}else if(sample == "U") {
						currentYUV.u = prevYUV.u;
					}else if(sample == "V") {
						currentYUV.v = prevYUV.v;
					}
				}
			}

		}

		return matrixYUV;

	}

	public static BufferedImage scaleImage(BufferedImage original,BufferedImage scaledImage, int width,int height, int newWidth,int newHeight, float scale_x,float scale_y){

        int newx = 0, newy = 0;
        
        for(float i = 0;i<height;i+=1/scale_y){
            newx = 0;
            for(float j = 0;j<width;j+=1/scale_x){
				if(newx<newWidth && newy<newHeight){
					
                    scaledImage.setRGB( newx,newy, original.getRGB(Math.round(j),Math.round(i)));
				}
                else
                    break;
                newx++;
            }
            newy++;
        }
        
        return scaledImage;
    }
	public static BufferedImage antiAliasing (BufferedImage input_image, int width,int height){
        int pix;
        float r = 0, g = 0, b = 0;
		BufferedImage aliased_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
		
				pix = input_image.getRGB(i,j);
                r   = 4*((pix & 0x00ff0000) >> 16);
                g = 4*((pix & 0x0000ff00) >> 8);
                b  = 4*(pix & 0x000000ff);
                if((i+1)<width){
                    pix = input_image.getRGB(i+1,j);
                    r   +=2* ((pix & 0x00ff0000) >> 16);
                    g +=2* ((pix & 0x0000ff00) >> 8);
                    b  +=2*(pix & 0x000000ff);
                }
                if((i-1)>=0){
                    pix = input_image.getRGB(i-1,j);
                    r   +=2* ((pix & 0x00ff0000) >> 16);
                    g +=2* ((pix & 0x0000ff00) >> 8);
                    b  +=2*(pix & 0x000000ff);
                }
                if((j+1)<height){
					
                    pix = input_image.getRGB(i,j+1);
                    r   +=2*((pix & 0x00ff0000) >> 16);
                    g +=2*((pix & 0x0000ff00) >> 8);
                    b  +=2*(pix & 0x000000ff);
                }
                if((j-1)>=0){
                    pix = input_image.getRGB(i,j-1);
                    r   +=2*((pix & 0x00ff0000) >> 16);
                    g +=2*((pix & 0x0000ff00) >> 8);
                    b  +=2*(pix & 0x000000ff);
                }
                if((i+1)<width && (j+1)<height){
                    pix = input_image.getRGB(i+1,j+1);
                    r   += (pix & 0x00ff0000) >> 16;
                    g += (pix & 0x0000ff00) >> 8;
                    b  +=  pix & 0x000000ff;
                }
                if((i+1)<width && (j-1)>=0){
                    pix = input_image.getRGB(i+1,j-1);
                    r   += (pix & 0x00ff0000) >> 16;
                    g += (pix & 0x0000ff00) >> 8;
                    b  +=  pix & 0x000000ff;
                }
                if((i-1)>=0 && (j+1)<height){
                    pix = input_image.getRGB(i-1,j+1);
                    r   += (pix & 0x00ff0000) >> 16;
                    g += (pix & 0x0000ff00) >> 8;
                    b  +=  pix & 0x000000ff;
                }
                if((i-1)>=0 && (j-1)>=0){
                    pix = input_image.getRGB(i-1,j-1);
                    r   += (pix & 0x00ff0000) >> 16;
                    g += (pix & 0x0000ff00) >> 8;
                    b  +=  pix & 0x000000ff;
                }

                pix = (Math.round(r/16) << 16) | (Math.round(g/16) << 8) | Math.round(b/16);
		
		aliased_image.setRGB(i,j,pix);
            }
        }
        return aliased_image;
    }
	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}
