package WGHxPERNAxBEAST.BeastlyCustomization.blocks;

import java.util.List;

import javax.annotation.Nullable;

import WGHxPERNAxBEAST.BeastlyCustomization.containers.DeathBoxContainer;
import WGHxPERNAxBEAST.BeastlyCustomization.tiles.DeathBoxTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DeathBoxBlock extends ContainerBlock implements IWaterLoggable {
	   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	   public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
	   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	   protected static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
	   protected static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
	   protected static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
	   protected static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
	   protected static final VoxelShape field_196315_B = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
	   private static final DeathBoxBlock.InventoryFactory<IInventory> field_220109_i = new DeathBoxBlock.InventoryFactory<IInventory>() {
		   
		   public IInventory forDouble(DeathBoxTile p_212855_1_, DeathBoxTile p_212855_2_) {
	         return new DoubleSidedInventory(p_212855_1_, p_212855_2_);
	      }

	      public IInventory forSingle(DeathBoxTile p_212856_1_) {
	         return p_212856_1_;
	      }
	   };
	   private static final DeathBoxBlock.InventoryFactory<INamedContainerProvider> field_220110_j = new DeathBoxBlock.InventoryFactory<INamedContainerProvider>() {
	      public INamedContainerProvider forDouble(final DeathBoxTile p_212855_1_, final DeathBoxTile p_212855_2_) {
	         final IInventory iinventory = new DoubleSidedInventory(p_212855_1_, p_212855_2_);
	         return new INamedContainerProvider() {
	            @Nullable
	            public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
	               if (p_212855_1_.canOpen(p_createMenu_3_) && p_212855_2_.canOpen(p_createMenu_3_)) {
	                  p_212855_1_.fillWithLoot(p_createMenu_2_.player);
	                  p_212855_2_.fillWithLoot(p_createMenu_2_.player);
	                  return DeathBoxContainer.createGeneric9X10(p_createMenu_1_, p_createMenu_2_, iinventory);
	               } else {
	                  return null;
	               }
	            }

	            public ITextComponent getDisplayName() {
	               if (p_212855_1_.hasCustomName()) {
	                  return p_212855_1_.getDisplayName();
	               } else {
	                  return (ITextComponent)(p_212855_2_.hasCustomName() ? p_212855_2_.getDisplayName() : new TranslationTextComponent("container.chestDouble"));
	               }
	            }
	         };
	      }

	      public INamedContainerProvider forSingle(DeathBoxTile p_212856_1_) {
	         return p_212856_1_;
	      }
	   };
	   private static PlayerEntity owner;
	   private static int playerXP;

	   public DeathBoxBlock(Block.Properties properties) {
	      super(properties);
	      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TYPE, ChestType.SINGLE).with(WATERLOGGED, Boolean.valueOf(false)));
	   }

	   /**
	    * @deprecated call via {@link IBlockState#hasCustomBreakingProgress()} whenever possible. Implementing/overriding is
	    * fine.
	    */
	   @OnlyIn(Dist.CLIENT)
	   public boolean hasCustomBreakingProgress(BlockState state) {
	      return true;
	   }

	   /**
	    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
	    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
	    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
	    */
	   public BlockRenderType getRenderType(BlockState state) {
	      return BlockRenderType.ENTITYBLOCK_ANIMATED;
	   }

	   /**
	    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	    * returns its solidified counterpart.
	    * Note that this method should ideally consider only the specific face passed in.
	    */
	   @SuppressWarnings("deprecation")
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
	      if (stateIn.get(WATERLOGGED)) {
	         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
	      }

	      if (facingState.getBlock() == this && facing.getAxis().isHorizontal()) {
	         ChestType chesttype = facingState.get(TYPE);
	         if (stateIn.get(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && stateIn.get(FACING) == facingState.get(FACING) && getDirectionToAttached(facingState) == facing.getOpposite()) {
	            return stateIn.with(TYPE, chesttype.opposite());
	         }
	      } else if (getDirectionToAttached(stateIn) == facing) {
	         return stateIn.with(TYPE, ChestType.SINGLE);
	      }

	      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	   }

	   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
	      if (state.get(TYPE) == ChestType.SINGLE) {
	         return field_196315_B;
	      } else {
	         switch(getDirectionToAttached(state)) {
	         case NORTH:
	         default:
	            return SHAPE_NORTH;
	         case SOUTH:
	            return SHAPE_SOUTH;
	         case WEST:
	            return SHAPE_WEST;
	         case EAST:
	            return SHAPE_EAST;
	         }
	      }
	   }

	   /**
	    * Returns a facing pointing from the given state to its attached double chest
	    */
	   public static Direction getDirectionToAttached(BlockState state) {
	      Direction direction = state.get(FACING);
	      return state.get(TYPE) == ChestType.LEFT ? direction.rotateY() : direction.rotateYCCW();
	   }

	   public BlockState getStateForPlacement(BlockItemUseContext context) {
	      ChestType chesttype = ChestType.SINGLE;
	      Direction direction = context.getPlacementHorizontalFacing().getOpposite();
	      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
	      boolean flag = context.isPlacerSneaking();
	      Direction direction1 = context.getFace();
	      if (direction1.getAxis().isHorizontal() && flag) {
	         Direction direction2 = this.getDirectionToAttach(context, direction1.getOpposite());
	         if (direction2 != null && direction2.getAxis() != direction1.getAxis()) {
	            direction = direction2;
	            chesttype = direction2.rotateYCCW() == direction1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
	         }
	      }

	      if (chesttype == ChestType.SINGLE && !flag) {
	         if (direction == this.getDirectionToAttach(context, direction.rotateY())) {
	            chesttype = ChestType.LEFT;
	         } else if (direction == this.getDirectionToAttach(context, direction.rotateYCCW())) {
	            chesttype = ChestType.RIGHT;
	         }
	      }

	      return this.getDefaultState().with(FACING, direction).with(TYPE, chesttype).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
	   }

	   @SuppressWarnings("deprecation")
	public IFluidState getFluidState(BlockState state) {
	      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	   }

	   /**
	    * Returns facing pointing to a chest to form a double chest with, null otherwise
	    */
	   @Nullable
	   private Direction getDirectionToAttach(BlockItemUseContext p_196312_1_, Direction p_196312_2_) {
	      BlockState blockstate = p_196312_1_.getWorld().getBlockState(p_196312_1_.getPos().offset(p_196312_2_));
	      return blockstate.getBlock() == this && blockstate.get(TYPE) == ChestType.SINGLE ? blockstate.get(FACING) : null;
	   }

	   /**
	    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
	    */
	   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
	      if (stack.hasDisplayName()) {
	         TileEntity tileentity = worldIn.getTileEntity(pos);
	         if (tileentity instanceof DeathBoxTile) {
	            ((DeathBoxTile)tileentity).setCustomName(stack.getDisplayName());
	         }
	      }
	      if (placer instanceof PlayerEntity) {
	    	  setBoxOwner((PlayerEntity)placer, false);
	      }
	   }

	   @SuppressWarnings("deprecation")
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
	      if (state.getBlock() != newState.getBlock()) {
	         TileEntity tileentity = worldIn.getTileEntity(pos);
	         if (tileentity instanceof IInventory) {
	            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
	            worldIn.updateComparatorOutputLevel(pos, this);
	         }

	         super.onReplaced(state, worldIn, pos, newState, isMoving);
	      }
	   }

	   public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
	      if (worldIn.isRemote) {
	         return true;
	      } else {
	    	  if (player.isCreative()) {
	    		     INamedContainerProvider inamedcontainerprovider = this.getContainer(state, worldIn, pos);
			         if (inamedcontainerprovider != null) {
			            player.openContainer(inamedcontainerprovider);
			            player.addStat(this.getOpenStat());
			         }
	    	  } else {
	    		  /*
	    		  if (owner != null) {
	    			  BeastlyCustomizationMain.logger.log(Level.INFO, "\"{} == {}\" evaluates to: {}", player.getName().getString(), owner.getName().getString(), (player.getName().getString().matches(owner.getName().getString())));
	    		  }
	    		  */
	    		  if (owner == null) {
		    		    //BeastlyCustomizationMain.logger.log(Level.INFO, "Death Box @{} has no owner", pos);
		    	  } else if (player.getName().getString().matches(owner.getName().getString())) {
			  			if (player.isSneaking()) {
			    		    //BeastlyCustomizationMain.logger.log(Level.INFO, "Transfering items to {}'s inventory", owner.getName().getString());
				  			player.sendMessage(new StringTextComponent("Transfering items to your inventory."));
				  			transferItemsToPlayer(state, worldIn, pos, player);
			  			}
				        INamedContainerProvider inamedcontainerprovider = this.getContainer(state, worldIn, pos);
				        if (inamedcontainerprovider != null) {
				           player.openContainer(inamedcontainerprovider);
				           player.addStat(this.getOpenStat());
				        }
			  	  } else {
			  			//BeastlyCustomizationMain.logger.log(Level.INFO, "{} can not access {}'s items", player.getName().getString(), owner.getName().getString());
			  			player.sendMessage(new StringTextComponent("You can not access this player's items. They are not yours."));
			  	  }	 
	    	 }
	         return true;
	      }
	   }

	   protected Stat<ResourceLocation> getOpenStat() {
	      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
	   }

	   @Nullable
	   public static <T> T func_220106_a(BlockState p_220106_0_, IWorld p_220106_1_, BlockPos p_220106_2_, boolean allowBlocked, DeathBoxBlock.InventoryFactory<T> p_220106_4_) {
	      TileEntity tileentity = p_220106_1_.getTileEntity(p_220106_2_);
	      if (!(tileentity instanceof DeathBoxTile)) {
	         return (T)null;
	      } else if (!allowBlocked && isBlocked(p_220106_1_, p_220106_2_)) {
	         return (T)null;
	      } else {
	         DeathBoxTile chesttileentity = (DeathBoxTile)tileentity;
	         ChestType chesttype = p_220106_0_.get(TYPE);
	         if (chesttype == ChestType.SINGLE) {
	            return p_220106_4_.forSingle(chesttileentity);
	         } else {
	            BlockPos blockpos = p_220106_2_.offset(getDirectionToAttached(p_220106_0_));
	            BlockState blockstate = p_220106_1_.getBlockState(blockpos);
	            if (blockstate.getBlock() == p_220106_0_.getBlock()) {
	               ChestType chesttype1 = blockstate.get(TYPE);
	               if (chesttype1 != ChestType.SINGLE && chesttype != chesttype1 && blockstate.get(FACING) == p_220106_0_.get(FACING)) {
	                  if (!allowBlocked && isBlocked(p_220106_1_, blockpos)) {
	                     return (T)null;
	                  }

	                  TileEntity tileentity1 = p_220106_1_.getTileEntity(blockpos);
	                  if (tileentity1 instanceof DeathBoxTile) {
	                     DeathBoxTile chesttileentity1 = chesttype == ChestType.RIGHT ? chesttileentity : (DeathBoxTile)tileentity1;
	                     DeathBoxTile chesttileentity2 = chesttype == ChestType.RIGHT ? (DeathBoxTile)tileentity1 : chesttileentity;
	                     return p_220106_4_.forDouble(chesttileentity1, chesttileentity2);
	                  }
	               }
	            }

	            return p_220106_4_.forSingle(chesttileentity);
	         }
	      }
	   }

	   @Nullable
	   public static IInventory getInventory(BlockState p_220105_0_, World p_220105_1_, BlockPos p_220105_2_, boolean allowBlocked) {
	      return func_220106_a(p_220105_0_, p_220105_1_, p_220105_2_, allowBlocked, field_220109_i);
	   }

	   @Nullable
	   public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
	      return func_220106_a(state, worldIn, pos, false, field_220110_j);
	   }

	   public TileEntity createNewTileEntity(IBlockReader worldIn) {
	      return new DeathBoxTile();
	   }

	   private static boolean isBlocked(IWorld p_220108_0_, BlockPos p_220108_1_) {
	      return isBelowSolidBlock(p_220108_0_, p_220108_1_) || isCatSittingOn(p_220108_0_, p_220108_1_);
	   }

	   private static boolean isBelowSolidBlock(IBlockReader p_176456_0_, BlockPos worldIn) {
	      BlockPos blockpos = worldIn.up();
	      return p_176456_0_.getBlockState(blockpos).isNormalCube(p_176456_0_, blockpos);
	   }

	   private static boolean isCatSittingOn(IWorld p_220107_0_, BlockPos p_220107_1_) {
	      List<CatEntity> list = p_220107_0_.getEntitiesWithinAABB(CatEntity.class, new AxisAlignedBB((double)p_220107_1_.getX(), (double)(p_220107_1_.getY() + 1), (double)p_220107_1_.getZ(), (double)(p_220107_1_.getX() + 1), (double)(p_220107_1_.getY() + 2), (double)(p_220107_1_.getZ() + 1)));
	      if (!list.isEmpty()) {
	         for(CatEntity catentity : list) {
	            if (catentity.isSitting()) {
	               return true;
	            }
	         }
	      }

	      return false;
	   }

	   /**
	    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
	    * is fine.
	    */
	   public boolean hasComparatorInputOverride(BlockState state) {
	      return true;
	   }

	   /**
	    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
	    * Implementing/overriding is fine.
	    */
	   public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
	      return Container.calcRedstoneFromInventory(getInventory(blockState, worldIn, pos, false));
	   }

	   /**
	    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	    * blockstate.
	    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
	    * fine.
	    */
	   public BlockState rotate(BlockState state, Rotation rot) {
	      return state.with(FACING, rot.rotate(state.get(FACING)));
	   }

	   /**
	    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	    * blockstate.
	    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
	    */
	   public BlockState mirror(BlockState state, Mirror mirrorIn) {
	      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	   }

	   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
	      builder.add(FACING, TYPE, WATERLOGGED);
	   }

	   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
	      return false;
	   }

	   interface InventoryFactory<T> {
	      T forDouble(DeathBoxTile p_212855_1_, DeathBoxTile p_212855_2_);

	      T forSingle(DeathBoxTile p_212856_1_);
	   }
	   
	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
		if (owner != null) {
			if (player.isCreative()) {
				return true;
			} else {
				if (player.getName().getString().matches(owner.getName().getString())) {
					return super.canHarvestBlock(state, world, pos, player);
				} else {
					return false;
				}
			}
		} else {
			return super.canHarvestBlock(state, world, pos, player);
		}
	}
	
	@Override
	public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
		return false;
	}
	
	@Override
	public void dropXpOnBlockBreak(World worldIn, BlockPos pos, int amount) {
		super.dropXpOnBlockBreak(worldIn, pos, playerXP);
		playerXP = 0;
	}
	   
	public static void transferItemsToPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
		PlayerInventory playerInvIn = playerIn.inventory;
		DeathBoxTile dbInv = (DeathBoxTile) worldIn.getBlockState(pos).getContainer(worldIn, pos);
		setInventoryContense(dbInv, playerInvIn);
		playerIn.giveExperiencePoints(playerXP);
		playerXP = 0;
	}
	
	public static void setBoxContense(DeathBoxTile dbInv, PlayerInventory playerInv) {
		for(int i = 0; i < playerInv.getSizeInventory(); /*- 1;*//*36;*/ i++) {
			ItemStack stack = playerInv.getStackInSlot(i);
			dbInv.setInventorySlotContents(i, stack);
			if (stack != null) {
				playerInv.deleteStack(stack);
			}
		}
	}
	
	private static void setInventoryContense(DeathBoxTile dbInv, PlayerInventory currentInv) {
		for(int i = 0; i < dbInv.getSizeInventory(); /*- 1;*//*36;*/ i++) {
			ItemStack stack = dbInv.getStackInSlot(i);
			ItemStack stack1 = currentInv.getStackInSlot(i);
			currentInv.setInventorySlotContents(i, stack);
			dbInv.setInventorySlotContents(i, stack1);
		}
	}
	
	public void setBoxOwner(PlayerEntity playerIn, Boolean shouldSaveLevels) {
		owner = playerIn;
		if (shouldSaveLevels) {
			int xp = (int)(playerIn.experienceTotal / 1.25);
			if (xp > 0) {
				playerXP = xp;
			} else {
				playerXP = 1;
			}
		}
	}	
	
	public PlayerEntity getOwner() {
		return owner;
	}
	
	public int getXP() {
		return playerXP;
	}
	
	public void updateOwner(PlayerEntity playerIn, int xpIn) {
		owner = playerIn;
		playerXP = xpIn;
	}
	   
}