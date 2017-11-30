package net.launcher.utils;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.launcher.components.Files;
import net.launcher.components.Frame;
import net.launcher.run.Settings;
import net.launcher.run.Starter;
import net.launcher.theme.Message;

public class BaseUtils
{
   public static final String empty = "";
   public static int logNumber = 0;
   public static ConfigUtils config = new ConfigUtils(File.separator + Settings.configFilename, getConfigName());

   public static Map<String, Font> fonts = new HashMap<String, Font>();
   public static Map<String, BufferedImage> imgs = new HashMap<String, BufferedImage>();
//   static String boundary;
   public static int servtype;

   public static BufferedImage getLocalImage(String name) 
   {
      try 
      {
         if(imgs.containsKey(name)) { return imgs.get(name); }

         BufferedImage e = ImageIO.read(BaseUtils.class.getResource("/assets/launcher/theme/" + name + ".png"));
         imgs.put(name, e);
         send("Opened local image: " + name + ".png");
         return e;
      }
      catch (Exception var2) 
      {
         sendErr("Fail to open local image: " + name + ".png");
         return getEmptyImage();
      }
   }

   public static BufferedImage getEmptyImage() 
   {
      return new BufferedImage(9, 9, BufferedImage.TYPE_INT_ARGB);
   }

   public static void send(String msg) 
   {
      if(Settings.debug) System.out.println(date() + "[Launcher thread/INFO]: " + msg);
   }

   public static void sendErr(String err) 
   {
      if(Settings.debug) System.err.println(date() + "[Launcher thread/WARN]: " + err);
   }

   public static void sendp(String msg) 
   {
      if(Settings.debug) System.out.println(date() + "[" + Thread.currentThread().getName() + "]: "+ msg);
   }

   public static void sendErrp(String err) 
   {
      if(Settings.debug) System.err.println(err);
   }

   public static boolean contains(int x, int y, int xx, int yy, int w, int h) 
   {
      return x >= xx && y >= yy && x < xx + w && y < yy + h;
   }

   public static File getConfigName() 
   {
      String home = System.getProperty("user.home", "");
      String path = File.separator + Settings.baseconf + File.separator + Settings.configFilename;
      switch(getPlatform())
      {
      	case 1:
      		return new File(System.getProperty("user.home", "") + path);
      	case 2:
      		String appData = System.getenv("APPDATA");	// путь к файлу конфига не может быть перенесен
    	  
      		if(appData != null) {
      			File dir = new File(appData + File.separator + Settings.baseconf);
      			if(!dir.exists()) {
      				dir.mkdirs();
      				File ff = new File(appData + path);
      				try {
      					ff.createNewFile();
      				} catch (IOException e) {
      					e.printStackTrace();
      				}
      			}
      			return new File(appData + path);
      		}
      		return new File(home + path);
      	case 3:
      		return new File(home, path);
      	default:
      		return new File(home + path);
      }
   }

   public static File getAssetsDir()
   {
      String home = System.getProperty("user.home", "");
      String path = File.separator + Settings.baseconf + File.separator;
      switch(getPlatform())
      {
      	case 1:	return new File(System.getProperty("user.home", "") + path);
      	case 2:
      		String appData = getPropertyString("drive");
      		if(appData==null){
      			appData = System.getenv("SYSTEMDRIVE");
      		}
      		if(appData != null) {
      			return new File(appData + path);
      		}
      		return new File(home + path);
      	case 3: return new File(home, path);
      	default: return new File(home + path);
      }
   }

   /** getClientName вызывать чревато - если еще не создалось окно лаунчера
    * 
    * @return File: путь к папке сервера (Не проекта!)
    */
   public static File getMcDir() {
      String home = System.getProperty("user.home", "");
      String path = Settings.pathconst.replaceAll("%SERVERNAME%", getClientName());
      switch(getPlatform()) {
      case 1: return new File(System.getProperty("user.home", ""), path);
      case 2:
         String appData = getPropertyString("drive");
         if(appData==null){
        	 appData = System.getenv("SYSTEMDRIVE");
         }
         if(appData != null) {
            return new File(appData, path);
         }
         return new File(home, path);
      case 3: return new File(home, path);
      default: return new File(home, path);
      }
   }
/**
 * Returns: linux,unix:0, solaris,sunos:1, windows:2,  macOS:3
 */
   public static int getPlatform() 
   {
      String osName = System.getProperty("os.name").toLowerCase();
      return osName.contains("win") ? 2 : (osName.contains("mac") ? 3 : (osName.contains("solaris") ? 1 : (osName.contains("sunos") ? 1 : (osName.contains("linux") ? 0 : (osName.contains("unix") ? 0 : 4)))));
   }

   public static String buildUrl(String s) 
   {
	   return Settings.http + Settings.domain + "/" + Settings.siteDir + "/" + s;
   }

   public static void setProperty(String s, Object value) {
      if(config.checkProperty(s).booleanValue()) {
         config.changeProperty(s, value);
      } else {
         config.put(s, value);
      }

   }

   public static String getPropertyString(String s) {
      return config.checkProperty(s).booleanValue() ? config.getPropertyString(s):null;
   }

   public static boolean getPropertyBoolean(String s) {
      return config.checkProperty(s).booleanValue()?config.getPropertyBoolean(s).booleanValue():false;
   }

   public static int getPropertyInt(String s) {
      return config.checkProperty(s).booleanValue()?config.getPropertyInteger(s).intValue():0;
   }

   public static int getPropertyInt(String s, int d) {
      File dir = new File(getAssetsDir().toString());
      if(!dir.exists()) {
         dir.mkdirs();
      }

      if(config.checkProperty(s).booleanValue()) {
         return config.getPropertyInteger(s).intValue();
      } else {
         setProperty(s, Integer.valueOf(d));
         return d;
      }
   }

   public static boolean getPropertyBoolean(String s, boolean b) {
      return config.checkProperty(s).booleanValue()?config.getPropertyBoolean(s).booleanValue():b;
   }

	static String boundary = PostUtils.randomString() + PostUtils.randomString() + PostUtils.randomString();
   
   public static String runHTTP(String URL, String params, boolean send)
   {
     HttpURLConnection ct = null;
     InputStream is;
     try
     {
       URL url = new URL(URL + params);
       ct = (HttpURLConnection)url.openConnection();
       ct.setRequestMethod("GET");
       ct.setRequestProperty("User-Agent", "KLauncher/0.9");
       ct.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
       ct.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
       ct.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
       ct.setUseCaches(false);
       ct.setDoInput(true);
       ct.setDoOutput(true);
       
       ct.connect();
       
       is = ct.getInputStream();
       
       BufferedReader rd = new BufferedReader(new InputStreamReader(is));
       Object localObject1 = null;
       StringBuilder response;
       try
       {
         response = new StringBuilder();
         String line;
         while ((line = rd.readLine()) != null) {
        	 response.append(line);
        	 response.append(System.lineSeparator());        	 
         }
       }
       catch (Throwable localThrowable1)
       {
         localObject1 = localThrowable1;
         throw localThrowable1;
       }
       finally
       {
         if (rd != null) {
           if (localObject1 != null) {
             try
             {
               rd.close();
             }
             catch (Throwable localThrowable2)
             {
               ((Throwable)localObject1).addSuppressed(localThrowable2);
             }
           } else {
             rd.close();
           }
         }
       }
       String str = response.toString();
       
       return str;
     }
     catch (Exception e)
     {
       is = null;
     }
     finally
     {
       if (ct != null) {
         ct.disconnect();
       }
     }
     return null;
   }
   /*
   public static String runHTTP(String URL, String params, boolean send) {
      HttpURLConnection ct = null;

      InputStream is;
      try {
         URL e = new URL(URL + params);
         ct = (HttpURLConnection)e.openConnection();
         ct.setRequestMethod("GET");
         ct.setRequestProperty("User-Agent", "Launcher/64.0");
         ct.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
         ct.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,* /*;q=0.8");
         ct.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
         ct.setUseCaches(false);
         ct.setDoInput(true);
         ct.setDoOutput(true);
         ct.connect();
         is = ct.getInputStream();
         BufferedReader str = new BufferedReader(new InputStreamReader(is));
         Throwable var8 = null;

         StringBuilder response;
         try {
            response = new StringBuilder();

            String line;
            while((line = str.readLine()) != null) {
               response.append(line);
            }
         } catch (Throwable var26) {
            var8 = var26;
            throw var26;
         } finally {
            if(str != null) {
               if(var8 != null) {
                  try {
                     str.close();
                  } catch (Throwable var25) {
                     var8.addSuppressed(var25);
                  }
               } else {
                  str.close();
               }
            }

         }

         String str1 = response.toString();
         String var31 = str1;
         return var31;
      } catch (Exception var28) {
         is = null;
      } finally {
         if(ct != null) {
            ct.disconnect();
         }

      }

      return is;
   }
*/
   public static String getURLSc(String script) {
      return getURL('/' + Settings.siteDir + '/' + script);
   }

   public static String[] getServerNames() {
      String[] error = new String[]{"Offline"};

      try {
         String e = runHTTP(getURLSc("servers.php"), "", false);
         if(e == null) {
            return error;
         } else if(!e.contains(", ")) {
            return error;
         } else {
        	 e = e.replaceAll(System.lineSeparator(), "");
            Settings.servers = e.replaceAll("<br>", "").split("<::>");
            String[] serversNames = new String[Settings.servers.length];

            for(int a = 0; a < Settings.servers.length; ++a) {
               serversNames[a] = Settings.servers[a].split(", ")[0];
            }

            return serversNames;
         }
      } catch (Exception var4) {
         return error;
      }
   }

   public static String getURL(String path) {
      return Settings.http + Settings.domain + path;
   }

   public static String getClientName() {
	   if(Frame.main == null) return null;
	   return Settings.useMulticlient ? Frame.main.servers.getSelected().replaceAll(" ", "") : "main";
   }

   public static void openURL(String url) {
      try {
         Object e = Class.forName("java.awt.Desktop").getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
         e.getClass().getMethod("browse", new Class[]{URI.class}).invoke(e, new Object[]{new URI(url)});
      } catch (Throwable var2) {
         ;
      }

   }

   public static void deleteDirectory(File file) {
      if(file.exists()) {
         if(file.isDirectory()) {
            File[] var1 = file.listFiles();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               File f = var1[var3];
               deleteDirectory(f);
            }

            file.delete();
         } else {
            file.delete();
         }

      }
   }

   public static BufferedImage getSkinImage(String name) {
      try {
         BufferedImage e = ImageIO.read(new URL(buildUrl("MinecraftSkins/" + name + ".png")));
         send("Skin loaded: " + buildUrl("MinecraftSkins/" + name + ".png"));
         return e;
      } catch (Exception var2) {
         sendErr("Skin not found: " + buildUrl("MinecraftSkins/" + name + ".png"));
         return getLocalImage("skin");
      }
   }

   public static BufferedImage getCloakImage(String name) {
      try {
         BufferedImage e = ImageIO.read(new URL(buildUrl("MinecraftCloaks/" + name + ".png")));
         send("Cloak loaded: " + buildUrl("MinecraftCloaks/" + name + ".png"));
         return e;
      } catch (Exception var2) {
         sendErr("Cloak not found: " + buildUrl("MinecraftCloaks/" + name + ".png"));
         return new BufferedImage(64, 32, 2);
      }
   }

   public static String execute(String surl, Object[] params) {
      try {
         send("Openning stream: " + surl);
         URL e = new URL(surl);
         InputStream is = PostUtils.post(e, params);
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
         StringBuffer response = new StringBuffer();

         String line;
         while((line = rd.readLine()) != null) {
            response.append(line);
         }

         rd.close();
         String str1 = response.toString().trim();
//         send("Stream opened for " + surl + " completed, return answer: ");
         return str1;
      } catch (Exception var8) {
         sendErr("Stream for " + surl + " not ensablished, return null");
         return null;
      }
   }

   public static Font getFont(String name, float size) {
      try {
         if(fonts.containsKey(name)) {
            return fonts.get(name).deriveFont(size);
         } else {
            Font e = null;
            send("Creating font: " + name);

            try {
               e = Font.createFont(0, BaseUtils.class.getResourceAsStream("/assets/launcher/theme/" + name + ".ttf"));
            } catch (Exception var6) {
               try {
                  e = Font.createFont(0, BaseUtils.class.getResourceAsStream("/assets/launcher/theme/" + name + ".otf"));
               } catch (Exception var5) {
                  var5.printStackTrace();
               }
            }

            fonts.put(name, e);
            return e.deriveFont(size);
         }
      } catch (Exception var7) {
         send("Failed create font!");
         throwException(var7, Frame.main);
         return null;
      }
   }

   public static void throwException(Exception e, Frame main) {
      e.printStackTrace();
      main.panel.removeAll();
      main.addFrameComp();
      StackTraceElement[] el = e.getStackTrace();
      main.panel.tmpString = "";
      main.panel.tmpString = main.panel.tmpString + e.toString() + "<:>";
      StackTraceElement[] var3 = el;
      int var4 = el.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         StackTraceElement i = var3[var5];
         main.panel.tmpString = main.panel.tmpString + "at " + i.toString() + "<:>";
      }

      main.panel.type = 3;
      main.repaint();
   }

   public static String[] pollServer(String ip, int port) {
	      Socket soc = null;
	      DataInputStream dis = null;
	      DataOutputStream dos = null;

	      String[] var6;
	      try {
	         soc = new Socket();
	         soc.setSoTimeout(6000);
	         soc.setTcpNoDelay(true);
	         soc.setTrafficClass(18);
	         soc.connect(new InetSocketAddress(ip, port), 6000);
	         dis = new DataInputStream(soc.getInputStream());
	         dos = new DataOutputStream(soc.getOutputStream());
	         dos.write(254);
	         if(dis.read() != 255) {
	            throw new IOException("Bad message");
	         }

	         String e = readString(dis, 256);
	         e.substring(3);
	        // int servtype;
			if(e.substring(0, 1).equalsIgnoreCase("§") && e.substring(1, 2).equalsIgnoreCase("1")) {
	            servtype = 1;
	            var6 = e.split(" ");
	            return var6;
			}
			servtype = 2;
			var6 = e.split("§");
	      } catch (Exception var27) {
	          var6 = new String[]{null, null, null};
	          return var6;
	       } finally {
	           try {
	               dis.close();
	            } catch (Exception var26) {
	               ;
	            }

	            try {
	               dos.close();
	            } catch (Exception var25) {
	               ;
	            }

	            try {
	               soc.close();
	            } catch (Exception var24) {
	               ;
	            }
	         }
	         return var6;
	      }

   public static int parseInt(String integer, int def) {
	      try {
	         return Integer.parseInt(integer.trim());
	      } catch (Exception var3) {
	         return def;
	      }
	}

   public static String readString(DataInputStream is, int d) throws IOException {
	      short word = is.readShort();
	      if(word > d) {
	         throw new IOException();
	      } else if(word < 0) {
	         throw new IOException();
	      } else {
	         StringBuilder res = new StringBuilder();

	         for(int i = 0; i < word; ++i) {
	            res.append(is.readChar());
	         }

	         return res.toString();
	      }
	}

   public static String genServerStatus(String[] args) {
	      if(servtype == 1) {
	         if(args[0] == null && args[1] == null && args[2] == null) {
	            return Message.serveroff;
	         }

	         if(args[4] != null && args[5] != null) {
	            if(args[4].equals(args[5])) {
	               return Message.serveroff.replace("%%", args[4]);
	            }

	            return Message.serveron.replace("%%", args[4]).replace("##", args[5]);
	         }
	      } else if(servtype == 2) {
	         if(args[0] == null && args[1] == null && args[2] == null) {
	            return Message.serveroff;
	         }

	         if(args[1] != null && args[2] != null) {
	            int i = args.length;
	            if(args[i - 2].equals(args[i - 1])) {
	               return Message.serveroff.replace("%%", args[i - 1]);
	            }

	            return Message.serveron.replace("%%", args[i - 2]).replace("##", args[i - 1]);
	         }
	      }
	      return Message.servererr;
   }
   public static BufferedImage genServerIcon(String[] args) {
	      return args[0] == null && args[1] == null && args[2] == null?Files.light.getSubimage(0, 0, Files.light.getHeight(), Files.light.getHeight()):(args[1] != null && args[2] != null?(args[1].equals(args[2])?Files.light.getSubimage(Files.light.getHeight(), 0, Files.light.getHeight(), Files.light.getHeight()):Files.light.getSubimage(Files.light.getHeight() * 2, 0, Files.light.getHeight(), Files.light.getHeight())):Files.light.getSubimage(Files.light.getHeight() * 3, 0, Files.light.getHeight(), Files.light.getHeight()));
	   }

	   public static void restart(){
		   restart(null);
	   }
	   
	   public static void restart(String path) {
	      send("Restarting launcher...");

	      try {
	         ArrayList<String> e = new ArrayList<String>();
	         e.add(System.getProperty("java.home") + "/bin/java");
	         e.add("-classpath");
	         e.add(Starter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
	         e.add(Starter.class.getCanonicalName());
	         ProcessBuilder pb = new ProcessBuilder(e);
	         Process process = pb.start();
	         if(process == null) {
	            throw new Exception("Launcher can\'t be started!");
	         }
	      } catch (Exception var3) {
	         var3.printStackTrace();
	         return;
	      }

	      System.exit(0);
	   }
	   public static String unix2hrd(long unix) {
		      return (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(new Date(unix * 1000L));
		   }

		   public void delete(File file) {
		      if(file.exists()) {
		         if(file.isDirectory()) {
		            File[] var2 = file.listFiles();
		            int var3 = var2.length;

		            for(int var4 = 0; var4 < var3; ++var4) {
		               File f = var2[var4];
		               this.delete(f);
		            }

		            file.delete();
		         } else {
		            file.delete();
		         }

		      }
		   }

		   public static boolean checkLink(String l) {
		      return !l.contains("#");
		   }

		   public static boolean existLink(String l) {
		      return l.contains("@");
		   }

		   public static void patchDir(URLClassLoader cl) {
		      if(Settings.patchDir) {
		         try {
		            Class<?> e = cl.loadClass("net.minecraft.client.Minecraft");
		            send("Changing client dir...");
		            Field[] var2 = e.getDeclaredFields();
		            int var3 = var2.length;

		            for(int var4 = 0; var4 < var3; ++var4) {
		               Field f = var2[var4];
		               if(f.getType().getName().equals("java.io.File") & Modifier.isPrivate(f.getModifiers()) & Modifier.isStatic(f.getModifiers())) {
		                  f.setAccessible(true);
		                  f.set((Object)null, getMcDir());
		                  send("Patching succesful, herobrine removed.");
		                  return;
		               }
		            }
		         } catch (Exception var6) {
		            sendErr("Client not patched");
		         }

		      }
		   }

		   public static void updateLauncher() throws Exception {
		      send("Launcher updater started...");

		      String uri = Settings.updateFile;
		      if(Frame.jar.compareTo(".exe")==0){
		    	  String x = System.getProperty("sun.arch.data.model");
		    	  if(x.compareTo("32")==0) uri += "32";
		    	  else uri +="64";
		      }
		      uri += Frame.jar;	// расширение текущего лаунчера
		      
		      send("Downloading file: "+uri);
		      BufferedInputStream is = new BufferedInputStream((new URL(uri)).openStream());
		      // один нюанс: при переходе с jar на exe нельзя менять расширение!
		      
		      FileOutputStream fos = new FileOutputStream(Starter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
//		      boolean bs = false;
		      byte[] buffer = new byte[65536];
		      MessageDigest md5 = MessageDigest.getInstance("MD5");

		      int bs1;
		      while((bs1 = is.read(buffer, 0, buffer.length)) != -1) {
		         fos.write(buffer, 0, bs1);
		         md5.update(buffer, 0, bs1);
		      }

		      is.close();
		      fos.close();
		      send("File downloaded: "+uri);
		      restart();
		   }

		   private static String date() {
		      Date now = new Date();
		      SimpleDateFormat formatter = new SimpleDateFormat("[HH:mm:ss]");
		      String s = formatter.format(now) + " ";
		      return s;
		   }

		   static {
		      config.load();
		      boundary = PostUtils.randomString() + PostUtils.randomString() + PostUtils.randomString();
		      servtype = 2;
		   }
}
	            		
	            		
	            		
	            		
	            		
	            		
	            		
	            		
	            		
	            		