package WGHxPERNAxBEAST.BeastlyCustomization.Entities;

import WGHxPERNAxBEAST.BeastlyCustomization.BeastlyCustomizationMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.DefendVillageTargetGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.MoveTowardsVillageGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ShowVillagerFlowerGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BabyGolem extends IronGolemEntity{
	
	public static double health = 120.0D;
	public static double move_speed = 0.7D;
	public static double knock_back_resist = 1.5D;
	public static float targeting_range = 35.0F;
	public static ResourceLocation textureLoc = BeastlyCustomizationMain.location("textures/entity/bs_golem.png");
	
	public BabyGolem(EntityType<? extends BabyGolem> type, World worldIn) {
		super(type, worldIn);
	}
	
	
	public BabyGolem setAttributes(EntityType<? extends BabyGolem> type, World worldIn, Double healthIn, Double speedIn, Double kb_resistIn, Float targetingRange, String name) {
		health = healthIn;
		move_speed = speedIn;
		knock_back_resist = kb_resistIn;
		targeting_range = targetingRange;
		textureLoc = BeastlyCustomizationMain.location("textures/entity/" + name + ".png");
		return new BabyGolem(type, worldIn);
	}
	
	@Override
	protected void registerGoals() {
	      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, move_speed * 2.6D, true));
	      this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, move_speed * 2.5D, targeting_range));
	      this.goalSelector.addGoal(2, new MoveTowardsVillageGoal(this, move_speed * 2.0D));
	      this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, move_speed * 2.0D, false, 4, () -> {
	         return false;
	      }));
	      this.goalSelector.addGoal(5, new ShowVillagerFlowerGoal(this));
	      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, move_speed * 2.0D));
	      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, targeting_range / 4.5F));
	      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
	      this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
	      this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
	      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, 5, false, false, (p_213619_0_) -> {
	         return p_213619_0_ instanceof IMob && !(p_213619_0_ instanceof CreeperEntity);
	      }));
	}
	
	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
	    this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(move_speed);
	    this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(knock_back_resist);
	    //this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.5D);
	}
	public ResourceLocation getTextureLoc() {
		return textureLoc;
	}
	
}
