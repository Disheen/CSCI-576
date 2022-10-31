
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	JFrame frame2;
	JLabel lbIm2;
	BufferedImage imgTwo;
	int width = 512; // default image width and height
	int height = 512;

	/**
	 * Read Image RGB
	 * Reads the image of given width and height at the given imgPath into the
	 * provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img, BufferedImage img2, int n) {
		try {
			int frameLength = width * height * 3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);
			int[][] r_mat = new int[512][512];
			int[][] g_mat = new int[512][512];
			int[][] b_mat = new int[512][512];

			int ind = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int r = bytes[ind];
					int g = bytes[ind + height * width];
					int b = bytes[ind + height * width * 2];

					r = r & 0xFF;
					g = g & 0xFF;
					b = b & 0xFF;

					r_mat[y][x] = r;
					g_mat[y][x] = g;
					b_mat[y][x] = b;

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x, y, pix);
					ind++;
				}
			}
			if (n > -1 && n <= 9) {
				

				int limit = 9 - n;
				int count = 0;
				int h = height;
				int[][] n_r_mat = new int[512][512];
				int[][] n_g_mat = new int[512][512];
				int[][] n_b_mat = new int[512][512];
				int w = h;

				while (count < limit && h > 1) {

					for (int i = 0; i < h; i++) {
						int p = 0;
						for (int j = 0; j < w; j += 2) {
							// System.out.println(j+h);
							int r1 = r_mat[i][j];
							int g1 = g_mat[i][j];
							int b1 = b_mat[i][j];

							int r2 = r_mat[i][j + 1];
							int g2 = g_mat[i][j + 1];
							int b2 = b_mat[i][j + 1];

							int r_sum = (r1 + r2) / 2;
							int g_sum = (g1 + g2) / 2;
							int b_sum = (b1 + b2) / 2;

							int r_diff = (r1 - r2) / 2;
							int g_diff = (g1 - g2) / 2;
							int b_diff = (b1 - b2) / 2;

							n_r_mat[i][p] = r_sum;
							n_g_mat[i][p] = g_sum;
							n_b_mat[i][p] = b_sum;

							n_r_mat[i][p + w / 2] = r_diff;
							n_g_mat[i][p + w / 2] = g_diff;
							n_b_mat[i][p + w / 2] = b_diff;

							p += 1;
						}

					}
					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							r_mat[i][j] = n_r_mat[i][j];
							g_mat[i][j] = n_g_mat[i][j];
							b_mat[i][j] = n_b_mat[i][j];
						}
					}
					// n_r_mat = new int[512][512];
					// n_g_mat = new int[512][512];
					// n_b_mat = new int[512][512];

					for (int j = 0; j < w; j++) {
						int p = 0;
						for (int i = 0; i < h; i += 2) {
							// System.out.println(i);
							// System.out.println(j+h);
							int r1 = r_mat[i][j];
							int g1 = g_mat[i][j];
							int b1 = b_mat[i][j];

							int r2 = r_mat[i + 1][j];
							int g2 = g_mat[i + 1][j];
							int b2 = b_mat[i + 1][j];

							int r_sum = (r1 + r2) / 2;
							int g_sum = (g1 + g2) / 2;
							int b_sum = (b1 + b2) / 2;

							int r_diff = (r1 - r2) / 2;
							int g_diff = (g1 - g2) / 2;
							int b_diff = (b1 - b2) / 2;

							n_r_mat[p][j] = r_sum;
							n_g_mat[p][j] = g_sum;
							n_b_mat[p][j] = b_sum;
							// System.out.println(p);
							// System.out.println("----------");
							// System.out.println(p+h/2);
							n_r_mat[p + h / 2][j] = r_diff;
							n_g_mat[p + h / 2][j] = g_diff;
							n_b_mat[p + h / 2][j] = b_diff;

							p += 1;
						}

					}
					h = h / 2;
					w = w / 2;

					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							r_mat[i][j] = n_r_mat[i][j];
							g_mat[i][j] = n_g_mat[i][j];
							b_mat[i][j] = n_b_mat[i][j];
						}
					}
					count += 1;
				}

				count = 0;

				n_r_mat = new int[512][512];
				n_g_mat = new int[512][512];
				n_b_mat = new int[512][512];
				for (int i = 0; i < 512; i++) {
					for (int j = 0; j < 512; j++) {
						n_r_mat[i][j] = 0;
						n_g_mat[i][j] = 0;
						n_b_mat[i][j] = 0;
					}
				}

				while (count < limit && h < 512) {

					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							if (j >= h) {

								r_mat[j][i] = 0;
								g_mat[j][i] = 0;
								b_mat[j][i] = 0;
							}
							if (i >= w) {

								r_mat[j][i] = 0;
								g_mat[j][i] = 0;
								b_mat[j][i] = 0;
							}
							if (j >= h && i >= w) {

								r_mat[j][i] = 0;
								g_mat[j][i] = 0;
								b_mat[j][i] = 0;
							}

						}
					}
					
					for (int j = 0; j < w; j++) {
						int p = 0;
						for (int i = 0; i < h; i++) {
							// System.out.println(i);
							// System.out.println(j+h);
							int r1 = r_mat[i][j];
							int g1 = g_mat[i][j];
							int b1 = b_mat[i][j];

							int r2 = r_mat[i + h][j];
							int g2 = g_mat[i + h][j];
							int b2 = b_mat[i + h][j];

							int r_sum = (r1 + r2);
							int g_sum = (g1 + g2);
							int b_sum = (b1 + b2);

							int r_diff = (r1 - r2);
							int g_diff = (g1 - g2);
							int b_diff = (b1 - b2);

							n_r_mat[p][j] = r_sum;
							n_g_mat[p][j] = g_sum;
							n_b_mat[p][j] = b_sum;
							// System.out.println(p);
							// System.out.println("----------");
							// System.out.println(p+h/2);
							n_r_mat[p + 1][j] = r_diff;
							n_g_mat[p + 1][j] = g_diff;
							n_b_mat[p + 1][j] = b_diff;

							p += 2;
						}

					}

					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							r_mat[i][j] = n_r_mat[i][j];
							g_mat[i][j] = n_g_mat[i][j];
							b_mat[i][j] = n_b_mat[i][j];
						}
					}
					h = h * 2;
					for (int i = 0; i < h; i++) {
						int p = 0;
						for (int j = 0; j < w; j++) {
							// System.out.println(j+h);
							int r1 = r_mat[i][j];
							int g1 = g_mat[i][j];
							int b1 = b_mat[i][j];

							int r2 = r_mat[i][j + w];
							int g2 = g_mat[i][j + w];
							int b2 = b_mat[i][j + w];

							int r_sum = (r1 + r2);
							int g_sum = (g1 + g2);
							int b_sum = (b1 + b2);

							int r_diff = (r1 - r2);
							int g_diff = (g1 - g2);
							int b_diff = (b1 - b2);

							n_r_mat[i][p] = r_sum;
							n_g_mat[i][p] = g_sum;
							n_b_mat[i][p] = b_sum;

							n_r_mat[i][p + 1] = r_diff;
							n_g_mat[i][p + 1] = g_diff;
							n_b_mat[i][p + 1] = b_diff;

							p += 2;
						}

					}
					

					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							r_mat[i][j] = n_r_mat[i][j];
							g_mat[i][j] = n_g_mat[i][j];
							b_mat[i][j] = n_b_mat[i][j];
						}
					}

					w = w * 2;
				
					count += 1;

					
				}

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int r = r_mat[y][x];
						int g = g_mat[y][x];
						int b = b_mat[y][x];

						r = r & 0xFF;
						g = g & 0xFF;
						b = b & 0xFF;

						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
						img2.setRGB(x, y, pix);
						ind++;
					}
				}
			} else if (n == -1) {
				int[][] u_r_mat = new int[512][512];
				int[][] u_g_mat = new int[512][512];
				int[][] u_b_mat = new int[512][512];
				
				int limit = 10 ;
				for(int m=0;m<=9;m++){
					limit--;

					
				for (int i = 0; i < 512; i++) {
					for (int j = 0; j < 512; j++) {
						u_r_mat[i][j]=r_mat[i][j];
						u_g_mat[i][j]=g_mat[i][j];
						u_b_mat[i][j]=b_mat[i][j];
					}
				}
				int count = 0;
				int h = height;
				int[][] n_r_mat = new int[512][512];
				int[][] n_g_mat = new int[512][512];
				int[][] n_b_mat = new int[512][512];

				int w = h;

				while (count < limit && h > 1) {

					for (int i = 0; i < h; i++) {
						int p = 0;
						for (int j = 0; j < w; j += 2) {
							// System.out.println(j+h);
							int r1 = u_r_mat[i][j];
							int g1 = u_g_mat[i][j];
							int b1 = u_b_mat[i][j];

							int r2 = u_r_mat[i][j + 1];
							int g2 = u_g_mat[i][j + 1];
							int b2 = u_b_mat[i][j + 1];

							int r_sum = (r1 + r2) / 2;
							int g_sum = (g1 + g2) / 2;
							int b_sum = (b1 + b2) / 2;

							int r_diff = (r1 - r2) / 2;
							int g_diff = (g1 - g2) / 2;
							int b_diff = (b1 - b2) / 2;

							n_r_mat[i][p] = r_sum;
							n_g_mat[i][p] = g_sum;
							n_b_mat[i][p] = b_sum;

							n_r_mat[i][p + w / 2] = r_diff;
							n_g_mat[i][p + w / 2] = g_diff;
							n_b_mat[i][p + w / 2] = b_diff;

							p += 1;
						}

					}
					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							u_r_mat[i][j] = n_r_mat[i][j];
							u_g_mat[i][j] = n_g_mat[i][j];
							u_b_mat[i][j] = n_b_mat[i][j];
						}
					}
					// n_r_mat = new int[512][512];
					// n_g_mat = new int[512][512];
					// n_b_mat = new int[512][512];

					for (int j = 0; j < w; j++) {
						int p = 0;
						for (int i = 0; i < h; i += 2) {
							// System.out.println(i);
							// System.out.println(j+h);
							int r1 = u_r_mat[i][j];
							int g1 = u_g_mat[i][j];
							int b1 = u_b_mat[i][j];

							int r2 = u_r_mat[i + 1][j];
							int g2 = u_g_mat[i + 1][j];
							int b2 = u_b_mat[i + 1][j];

							int r_sum = (r1 + r2) / 2;
							int g_sum = (g1 + g2) / 2;
							int b_sum = (b1 + b2) / 2;

							int r_diff = (r1 - r2) / 2;
							int g_diff = (g1 - g2) / 2;
							int b_diff = (b1 - b2) / 2;

							n_r_mat[p][j] = r_sum;
							n_g_mat[p][j] = g_sum;
							n_b_mat[p][j] = b_sum;
							
							n_r_mat[p + h / 2][j] = r_diff;
							n_g_mat[p + h / 2][j] = g_diff;
							n_b_mat[p + h / 2][j] = b_diff;

							p += 1;
						}

					}
					h = h / 2;
					w = w / 2;

					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							u_r_mat[i][j] = n_r_mat[i][j];
							u_g_mat[i][j] = n_g_mat[i][j];
							u_b_mat[i][j] = n_b_mat[i][j];
						}
					}
					count += 1;
				}

				count = 0;

				n_r_mat = new int[512][512];
				n_g_mat = new int[512][512];
				n_b_mat = new int[512][512];
				for (int i = 0; i < 512; i++) {
					for (int j = 0; j < 512; j++) {
						n_r_mat[i][j] = 0;
						n_g_mat[i][j] = 0;
						n_b_mat[i][j] = 0;
						
					}
				}

				while (count < limit && h < 512) {

					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							if (j >= h) {

								u_r_mat[j][i] = 0;
								u_g_mat[j][i] = 0;
								u_b_mat[j][i] = 0;
							}
							if (i >= w) {

								u_r_mat[j][i] = 0;
								u_g_mat[j][i] = 0;
								u_b_mat[j][i] = 0;
							}
							if (j >= h && i >= w) {

								u_r_mat[j][i] = 0;
								u_g_mat[j][i] = 0;
								u_b_mat[j][i] = 0;
							}

						}
					}
				

					for (int i = 0; i < h; i++) {
						int p = 0;
						for (int j = 0; j < w; j++) {
							int r1 = u_r_mat[i][j];
							int g1 = u_g_mat[i][j];
							int b1 = u_b_mat[i][j];

							int r2 = u_r_mat[i][j + w];
							int g2 = u_g_mat[i][j + w];
							int b2 = u_b_mat[i][j + w];

							int r_sum = (r1 + r2);
							int g_sum = (g1 + g2);
							int b_sum = (b1 + b2);

							int r_diff = (r1 - r2);
							int g_diff = (g1 - g2);
							int b_diff = (b1 - b2);

							n_r_mat[i][p] = r_sum;
							n_g_mat[i][p] = g_sum;
							n_b_mat[i][p] = b_sum;

							n_r_mat[i][p + 1] = r_diff;
							n_g_mat[i][p + 1] = g_diff;
							n_b_mat[i][p + 1] = b_diff;

							p += 2;
						}

					}

					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							u_r_mat[i][j] = n_r_mat[i][j];
							u_g_mat[i][j] = n_g_mat[i][j];
							u_b_mat[i][j] = n_b_mat[i][j];
						}
					}

					w = w * 2;
					for (int j = 0; j < w; j++) {
						int p = 0;
						for (int i = 0; i < h; i++) {
							// System.out.println(i);
							// System.out.println(j+h);
							int r1 = u_r_mat[i][j];
							int g1 = u_g_mat[i][j];
							int b1 = u_b_mat[i][j];

							int r2 = u_r_mat[i + h][j];
							int g2 = u_g_mat[i + h][j];
							int b2 = u_b_mat[i + h][j];

							int r_sum = (r1 + r2);
							int g_sum = (g1 + g2);
							int b_sum = (b1 + b2);

							int r_diff = (r1 - r2);
							int g_diff = (g1 - g2);
							int b_diff = (b1 - b2);

							n_r_mat[p][j] = r_sum;
							n_g_mat[p][j] = g_sum;
							n_b_mat[p][j] = b_sum;
							// System.out.println(p);
							// System.out.println("----------");
							// System.out.println(p+h/2);
							n_r_mat[p + 1][j] = r_diff;
							n_g_mat[p + 1][j] = g_diff;
							n_b_mat[p + 1][j] = b_diff;

							p += 2;
						}

					}

					for (int i = 0; i < 512; i++) {
						for (int j = 0; j < 512; j++) {
							u_r_mat[i][j] = n_r_mat[i][j];
							u_g_mat[i][j] = n_g_mat[i][j];
							u_b_mat[i][j] = n_b_mat[i][j];
						}
					}
					h = h * 2;
					count += 1;

					
				}

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int r = u_r_mat[y][x];
						int g = u_g_mat[y][x];
						int b = u_b_mat[y][x];

						r = r & 0xFF;
						g = g & 0xFF;
						b = b & 0xFF;

						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
						imgOne.setRGB(x, y, pix);
						ind++;
					}
				}
				frame = new JFrame();
				GridBagLayout gLayout = new GridBagLayout();
				GridBagConstraints c = new GridBagConstraints();
				frame.getContentPane().setLayout(gLayout);
		
				lbIm1 = new JLabel(new ImageIcon(imgOne));
		
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
				
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showIms(String[] args) {

		// Read a parameter from command line
		int n = Integer.parseInt(args[1]);
		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		imgTwo = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, args[0], imgOne, imgTwo, n);
		if (n >= 0 && n <= 9) {
			// Use label to display the image
			GridBagLayout gLayout = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			frame2 = new JFrame();
			frame2.getContentPane().setLayout(gLayout);

			lbIm2 = new JLabel(new ImageIcon(imgTwo));

			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.CENTER;
			c.weightx = 0.5;
			c.gridx = 0;
			c.gridy = 0;

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			frame2.getContentPane().add(lbIm2, c);

			frame2.pack();
			frame2.setVisible(true);
		}
	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}
