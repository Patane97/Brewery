package com.Patane.Brewery.Sequencer;

import com.Patane.Brewery.YAML.BreweryYAML;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

public class SequencesYML extends BreweryYAML{

	public SequencesYML() {
		super("sequences.yml", "sequences", "YML File for each sequence\nExample:");
	}

	@Override
	public void save() {
	}

	@Override
	public void load() {
		for(String seqName : getSelect().getKeys(false)){
//			int currentDelay = 0;
			setSelect(seqName);
			for(String keyName : getSelect().getKeys(false)){
				Messenger.debug(Msg.WARNING, keyName);
//				switch(keyName){
//				case "Effect":
//					// Need to work in Trigger somehow
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
