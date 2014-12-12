package appGame.Puzzle.CombineMode;

import java.io.IOException;
import java.util.*;
  
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public  class AudioPlayer
{
	public static float soundVolume = 1;
	public float musicVolume = 1;
	private SoundPool defaultSoundPool; 	//音效
	private SoundPool soundPool; 		    //音效
	private MediaPlayer[] soundMusicPlayer;	//SoundPool無法撥放長音效必須重複播放檔案因此使用此替代(但是佔用資源較大)
	private MediaPlayer musicPlayer; 		//背景音樂
	private int nowPlaySoundID;
	private int nowPlayMusicID;
	private int nowPlaySoundMusicID;
	public Context context;
	private HashMap<Integer, Integer> soundPoolMap; 
	private HashMap<Integer,String> musicPlayerMap;
	private HashMap<Integer,String> soundMusicPlayerMap;
	
	public AudioPlayer(Context ct)
	{
		context = ct;
		defaultSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0); 
		soundPoolMap = new HashMap<Integer, Integer>();
		musicPlayerMap = new HashMap<Integer, String>();
		soundMusicPlayerMap = new HashMap<Integer, String>();
		musicPlayer = new MediaPlayer();
		
		soundMusicPlayer = new MediaPlayer[3];
		for(int i=0;i<soundMusicPlayer.length;i++)
			soundMusicPlayer[i]=new MediaPlayer();
	}

	public void loadSounds(String file,int ID)
	{
		if(soundPool==null)	
			soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap.put(ID, soundPool.load("/sdcard/.XinGame/"+file,ID)); 
	}
 
	public void loadAssetsSounds(Activity mother,String file,int ID)  
	{  
			AssetFileDescriptor afd = null; 
		   try  
		      {  
		    	 afd = mother.getResources().getAssets().openFd(file);
		      }  
		      catch  (Exception e)  
		      {  
		          System.out.println(e);
		      }   
		
		if(soundPool==null)	
			soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		
		soundPoolMap.put(ID, soundPool.load(afd, ID)); 
	}
	
	public void loadMusic(String file,int ID)
	{
			musicPlayerMap.put(ID, file);
	}
	
	public void loadSoundMusic(String file,int ID)
	{
		soundMusicPlayerMap.put(ID, file);
	}
	
	public void setSoundVolume(float volume)
	{//volume值介於 0~1之間
		soundVolume = volume;
		if(soundPool==null)	
			soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		soundPool.setVolume(nowPlaySoundID, soundVolume, soundVolume);
		
		for(int i=0;i<soundMusicPlayer.length;i++)
		{
			if(soundMusicPlayer[i]!=null)
				soundMusicPlayer[i].setVolume(soundVolume, soundVolume);
		}
	}
	
	public void setMusicVolume(float volume)
	{//volume值介於 0~1之間
		musicVolume = volume;
		musicPlayer.setVolume(musicVolume, musicVolume);
	}
	
	public void playSound(int streamID,int loop)
	{//
		nowPlaySoundID = streamID;
		if(soundPool==null)	
			soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		soundPool.play(soundPoolMap.get(streamID), soundVolume, soundVolume, 1, loop, 1);
	}
	
	public void playSoundMusic(int sChannel,int streamID,boolean loop)
	{
		try {
		if(sChannel>=soundMusicPlayer.length)
			return;
		
		/*
		if(streamID == nowPlaySoundMusicID)
		{
			if(soundMusicPlayer[sChannel]!=null)
			{
				soundMusicPlayer[sChannel].setVolume(soundVolume, soundVolume);
				soundMusicPlayer[sChannel].setLooping(loop);
				return;
			}
		}
		
		nowPlaySoundMusicID = streamID;
		*/
		
		if(soundMusicPlayer[sChannel]!=null)
			soundMusicPlayer[sChannel].release();
		
		soundMusicPlayer[sChannel] = new MediaPlayer();
		soundMusicPlayer[sChannel].setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		
			
			soundMusicPlayer[sChannel].setDataSource("/sdcard/.XinGame/"+soundMusicPlayerMap.get(streamID));
			soundMusicPlayer[sChannel].setLooping(loop);
			soundMusicPlayer[sChannel].setVolume(soundVolume, soundVolume);
			soundMusicPlayer[sChannel].prepare();
			soundMusicPlayer[sChannel].start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void playMusic(int streamID,boolean loop)
	{//
		nowPlayMusicID = streamID;
		
		if(musicPlayer!=null)
			musicPlayer.release();
		
		musicPlayer = new MediaPlayer();
		musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		try {
			
			musicPlayer.setDataSource("/sdcard/.XinGame/"+musicPlayerMap.get(streamID));
			musicPlayer.setLooping(loop);
			musicPlayer.setVolume(musicVolume, musicVolume);
			musicPlayer.prepare();
			musicPlayer.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void stopSoundMusic(int sChannel)
	{//
		try
		{
			if(soundMusicPlayer[sChannel].isPlaying())
			{
				soundMusicPlayer[sChannel].stop();
				nowPlaySoundMusicID = 0;
			}
		}
		catch(Exception e){}
	}
	
	public void stopMusic()
	{//
		musicPlayer.stop();
	}
	
	public void stopSound(int streamID)
	{//
		soundPool.stop(soundPoolMap.get(streamID));
	}
	
	public boolean soundMusicIsPlaying(int sChannel)
	{
		try
		{
			return soundMusicPlayer[sChannel].isPlaying();
		}
		catch(Exception e){}
		return false;
		
	}
	
	public boolean soundMusicIsLooping(int sChannel)
	{
		try
		{
			return soundMusicPlayer[sChannel].isLooping();
		}
		catch(Exception e){}
		return false;
		
	}
	
	public void release()
	{
		try
		{
		
			if(soundPool!=null)
			{
				soundPool.release();
				soundPool=null;
			}
			for(int i=0;i<soundMusicPlayer.length;i++)
			{
				if(soundMusicPlayer[i]!=null)
				{
					soundMusicPlayer[i].release();
					soundMusicPlayer[i] = null;
				}
			}
		
		}
		catch(Exception e){System.out.println(e);}
	}
	
	public void pause()
	{
		if(soundPool!=null)
			soundPool.setVolume(nowPlaySoundID, 0, 0);
		musicPlayer.setVolume(0, 0);
		for(int i=0;i<soundMusicPlayer.length;i++)
		{
			if(soundMusicPlayer[i]!=null && soundMusicPlayer[i].isPlaying())
				soundMusicPlayer[i].setVolume(0, 0);
		}
	}
	
	public void resume()
	{
		setSoundVolume(soundVolume);
		setMusicVolume(musicVolume);
	}
}


