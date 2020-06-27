package com.Patane.Brewery.CustomEffects;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import com.Patane.Brewery.Brewery;
import com.Patane.util.YAML.TypeParsable;
import com.Patane.util.annotations.TypeDescriber;
import com.Patane.util.general.StringsUtil.LambdaStrings;

@TypeDescriber(
		name="Modifier",
		desc="Modifies each living entity hit by the attached effect in a unique way.")
public abstract class Modifier extends TypeParsable {
	
	public Modifier() {
		super();
	}
	public Modifier(Map<String, String> fields) {
		super(fields);
	}

	public abstract void modify(ModifierInfo info);
	
	public void damage(LivingEntity damagee, LivingEntity damager, double amount) {
		damagee.setMetadata("Brewery_DAMAGE", new FixedMetadataValue(Brewery.getInstance(), null));
		damagee.damage(amount, damager);
	}
	
	/* ================================================================================
	 * ChatStringable Methods
	 * ================================================================================
	 */
	@Override
	public LambdaStrings layout() {
		// Example: &2Type: &7Name
		return s -> "&2"+s[0]+"&2: &7"+s[1];
	}
	
	
	public static class ModifierInfo {
		private final Location impact;
		private final LivingEntity targeter;
		private final LivingEntity target;

		public ModifierInfo(Location impact, LivingEntity targeter, LivingEntity target) {
			this.impact = impact;
			this.targeter = targeter;
			this.target = target;
		}

		public Location getImpact() {
			return impact;
		}
		
		public LivingEntity getTargeter() {
			return targeter;
		}
		
		public LivingEntity getTarget() {
			return target;
		}
	}
}
