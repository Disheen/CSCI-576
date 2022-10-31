
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class ImageDisplay {

	/**
	 * Read Image RGB
	 * Reads the image of given width and height at the given imgPath into the
	 * provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String fgImgPath,String n_fgImgPath, String bgImgPath, BufferedImage img, int mode,int i) {
		try {
			int frameLength = width * height * 3;

			File fgFile = new File(fgImgPath);
			File n_fgFile = new File(n_fgImgPath);
			File bgFile = new File(bgImgPath);
			RandomAccessFile raf = new RandomAccessFile(fgFile, "r");
			RandomAccessFile raf2 = new RandomAccessFile(bgFile, "r");
			RandomAccessFile raf3 = new RandomAccessFile(n_fgFile, "r");
			raf.seek(0);
			raf2.seek(0);
			raf3.seek(0);

			long len = frameLength;
			byte[] fgBytes = new byte[(int) len];
			byte[] n_fgBytes = new byte[(int) len];
			byte[] bgBytes = new byte[(int) len];

			raf.read(fgBytes);
			raf2.read(bgBytes);
			raf3.read(n_fgBytes);

			int ind = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					byte a = 0;
					int fg_r = fgBytes[ind];
					int fg_g = fgBytes[ind + height * width];
					int fg_b = fgBytes[ind + height * width * 2];

					fg_r = fg_r & 0xff;
					fg_g = fg_g & 0xFF;
					fg_b = fg_b & 0xFF;
					if (mode == 1) {
						int[] u_fg = mode_1(fg_r, fg_g, fg_b, bgBytes, ind, width, height);
						fg_r = u_fg[0];
						fg_g = u_fg[1];
						fg_b = u_fg[2];
					}
					else if (mode==0){
						int[] u_0_fg=mode_0(fgBytes,n_fgBytes, bgBytes, ind, width, height);
						fg_r=u_0_fg[0];
						fg_g=u_0_fg[1];
						fg_b=u_0_fg[2];
					}

					int pix = 0xff000000 | ((fg_r & 0xff) << 16) | ((fg_g & 0xff) << 8) | (fg_b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x, y, pix);
					ind++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showIms(int i, String foreground, String background, int mode,long a) {
		JFrame frame;
		JLabel lbIm1;
		BufferedImage imgOne;
		int width = 640; // default image width and height
		int height = 480;
		String pref_name;
		String next_pref_name=".0478";
		if (i < 10) {
			pref_name = ".000" + String.valueOf(i);
		} else if (i < 100) {
			pref_name = ".00" + String.valueOf(i);
		} else {
			pref_name = ".0" + String.valueOf(i);
		}
		if (i+1 < 10) {
			next_pref_name = ".000" + String.valueOf(i+1);
		} else if (i+1 < 100) {
			next_pref_name = ".00" + String.valueOf(i+1);
		} else if(i+1<480){
			next_pref_name = ".0" + String.valueOf(i+1);
		}
		String fg = foreground + "/" + foreground + pref_name + ".rgb";
		String n_fg = foreground + "/" + foreground + next_pref_name + ".rgb";
		String bg = background + "/" + background + pref_name + ".rgb";

		// Read a parameter from command line

		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, fg,n_fg, bg, imgOne, mode,i);

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(imgOne));

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
		long b=System.currentTimeMillis();
		long sleepTime=1000/24-(b-a)-1;
		try{
			Thread.sleep(sleepTime);
		}
		catch (Exception e){
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < 480; i++) {
			long a=System.currentTimeMillis();
			ImageDisplay ren = new ImageDisplay();
			ren.showIms(i, args[0], args[1], Integer.parseInt(args[2]),a);
		}
	}

	public static double[] RGBtoHSV(double r, double g, double b) {

		double h, s, v;

		double min, max, delta;

		min = Math.min(Math.min(r, g), b);
		max = Math.max(Math.max(r, g), b);

		v = max;

		delta = max - min;

		// S
		if (max != 0)
			s = delta / max;
		else {
			s = 0;
			h = -1;
			return new double[] { h, s, v };
		}

		// H
		if (r == max)
			h = (g - b) / delta; 
		else if (g == max)
			h = 2 + (b - r) / delta; 
		else
			h = 4 + (r - g) / delta; 

		h *= 60; 

		if (h < 0)
			h += 360;

		h = h * 1.0;
		s = s;
		v = (v / 256.0);
		return new double[] { h, s, v };
	}

	public static int[] mode_1(int fg_r, int fg_g, int fg_b, byte[] bgBytes, int ind, int width, int height) {
		double[] fg_hsv = RGBtoHSV(fg_r, fg_g, fg_b);
		int bg_r = bgBytes[ind];
		int bg_g = bgBytes[ind + height * width];
		int bg_b = bgBytes[ind + height * width * 2];

		bg_r = bg_r & 0xff;
		bg_g = bg_g & 0xFF;
		bg_b = bg_b & 0xFF;

		if (fg_hsv[0] >= 90 && fg_hsv[0] <= 160 && fg_hsv[1] >= 0.25 && fg_hsv[1] <= 1 && fg_hsv[2] >= 0.25
				&& fg_hsv[2] <= 1) {
			fg_r = bg_r;
			fg_g = bg_g;
			fg_b = bg_b;
		}
		return new int[] { fg_r, fg_g, fg_b };
	}

	public static int[] mode_0( byte[] fgBytes,byte[] n_fgBytes, byte[] bgBytes, int ind, int width, int height) {
		// double[] fg_hsv = RGBtoHSV(fg_r, fg_g, fg_b);
		int bg_r = bgBytes[ind];
		int bg_g = bgBytes[ind + height * width];
		int bg_b = bgBytes[ind + height * width * 2];

		bg_r = bg_r & 0xff;
		bg_g = bg_g & 0xFF;
		bg_b = bg_b & 0xFF;

		int fg_r = fgBytes[ind];
		int fg_g = fgBytes[ind + height * width];
		int fg_b = fgBytes[ind + height * width * 2];

		fg_r = fg_r & 0xff;
		fg_g = fg_g & 0xFF;
		fg_b = fg_b & 0xFF;

		int n_fg_r = n_fgBytes[ind];
		int n_fg_g = n_fgBytes[ind + height * width];
		int n_fg_b = n_fgBytes[ind + height * width * 2];

		n_fg_r = n_fg_r & 0xff;
		n_fg_g = n_fg_g & 0xFF;
		n_fg_b = n_fg_b & 0xFF;

		
		
		
	
		if (Math.abs(fg_r-n_fg_r)<=10 && Math.abs(fg_g-n_fg_g)<=10 && Math.abs(fg_b-n_fg_b)<=10 ) {
			fg_r = bg_r;
			fg_g = bg_g;
			fg_b = bg_b;
		}
		return new int[] { fg_r, fg_g, fg_b };
	}

}
