package com.Patane.Brewery.Sequencer;

import org.bukkit.plugin.Plugin;

import com.Patane.util.YML.BasicYML;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

public class SequencesYML extends BasicYML{

	public SequencesYML(Plugin plugin) {
		super(plugin, "sequences.yml", "sequences");
	}

	@Override
	public void save() {
	}

	@Override
	public void load() {
		for(String seqName : header.getKeys(false)){
//			int currentDelay = 0;
			setHeader(seqName);
			for(String keyName : header.getKeys(false)){
				Messenger.debug(Msg.WARNING, keyName);
//				switch(keyName){
//				case "Effect":
//					// Need to work in EffectType somehow
//					break;
//				case "Particle":
//					break;
//				case "Sound":
//					break;
//				case "Delay":
//					currentDelay += header.getInt(keyName);
//					break;
//				default:
//					break;
//				}
			}
		}
	}

}
