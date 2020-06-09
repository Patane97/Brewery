package com.Patane.Brewery.Sequencer;

import com.Patane.Brewery.YAML.BreweryYAML;

public class SequencesYML extends BreweryYAML{

	public SequencesYML() {
		super("sequences", "sequences");
	}

	@Override
	public void save() {
	}

	@Override
	public void load() {
		for(String seqName : getSelect().getKeys(false)) {
//			int currentDelay = 0;
			setSelect(seqName);
//			for(String keyName : getSelect().getKeys(false)) {
////				switch(keyName) {
////				case "Effect":
////					// Need to work in Trigger somehow
////					break;
////				case "Particle":
////					break;
////				case "Sound":
////					break;
////				case "Delay":
////					currentDelay += header.getInt(keyName);
////					break;
////				default:
////					break;
////				}
//			}
		}
	}

}
