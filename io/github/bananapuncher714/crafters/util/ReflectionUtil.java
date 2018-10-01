package io.github.bananapuncher714.crafters.util;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.implementation.API.CraftInventoryManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * Yet another simplified reflection utility to load the proper {@link CraftInventoryManager} for each version;
 * Created on 2017-12-07
 * 
 * @author BananaPuncher714
 */
public final class ReflectionUtil {
	private static Class< CraftInventoryManager > containerManager = null;
	private static String version;

	private static final HashMap< String, Class<?> > classCache;
	private static final HashMap< String, Method > methodCache;
	private static final HashMap< Class< ? >, Constructor< ? > > constructorCache;
	private static Field connection;
	
	static {
		version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			containerManager = ( Class< CraftInventoryManager > ) Class.forName( "io.github.bananapuncher714.crafters.implementation." + version + ".ContainerManager_" + version );
		} catch ( ClassNotFoundException e) {
			PublicCrafters.getInstance().getLogger().severe( "'" + version + "' is not implemented at the moment! Please contact BananaPuncher714 for future support!" );
		}
		
		classCache = new HashMap< String, Class<?> >();
		try {
			classCache.put( "CraftPlayer", Class.forName( "org.bukkit.craftbukkit." + version + ".entity.CraftPlayer" ) );
			classCache.put( "PlayerConnection", Class.forName( "net.minecraft.server." + version + "." + "PlayerConnection" ) );
			
			classCache.put( "CraftWorld", Class.forName( "org.bukkit.craftbukkit." + version + "." + "CraftWorld" ) );
			classCache.put( "CraftItemStack", Class.forName( "org.bukkit.craftbukkit." + version + ".inventory." + "CraftItemStack" ) );
			classCache.put( "CraftArmorStand", Class.forName( "org.bukkit.craftbukkit." + version + ".entity.CraftArmorStand" ) );
			classCache.put( "ItemStack", Class.forName( "net.minecraft.server." + version + "." + "ItemStack" ) );
			
			classCache.put( "EntityArmorStand", Class.forName( "net.minecraft.server." + version + "." + "EntityArmorStand" ) );
			classCache.put( "EntityLiving", Class.forName( "net.minecraft.server." + version + "." + "EntityLiving" ) );
			classCache.put( "EntityPlayer", Class.forName( "net.minecraft.server." + version + "." + "EntityPlayer" ) );

			classCache.put( "Packet", Class.forName( "net.minecraft.server." + version + "." + "Packet" ) );
			classCache.put( "PacketPlayOutEntityDestroy", Class.forName( "net.minecraft.server." + version + "." + "PacketPlayOutEntityDestroy" ) );
			classCache.put( "PacketPlayOutEntityEquipment", Class.forName( "net.minecraft.server." + version + "." + "PacketPlayOutEntityEquipment" ) );
			classCache.put( "PacketPlayOutSpawnEntityLiving", Class.forName( "net.minecraft.server." + version + "." + "PacketPlayOutSpawnEntityLiving" ) );
			classCache.put( "World", Class.forName( "net.minecraft.server." + version + "." + "World" ) );
			
			if ( !version.equalsIgnoreCase( "v1_8_R3" ) ) {
				classCache.put( "EnumItemSlot", Class.forName( "net.minecraft.server." + version + "." + "EnumItemSlot" ) );
			}
		} catch ( ClassNotFoundException e ) {
			e.printStackTrace();
		}

		methodCache = new HashMap< String, Method >();
		try {
			methodCache.put( "asNMSCopy", getNMSClass( "CraftItemStack" ).getMethod( "asNMSCopy", ItemStack.class ) );
			methodCache.put( "asBukkitCopy", getNMSClass( "CraftItemStack" ).getMethod( "asBukkitCopy", getNMSClass( "ItemStack" ) ) );
			
			methodCache.put( "getWorldHandle", getNMSClass( "CraftWorld" ).getMethod( "getHandle" ) );
			methodCache.put( "getHandle", getNMSClass( "CraftPlayer" ).getMethod( "getHandle" ) );
			methodCache.put( "sendPacket", getNMSClass( "PlayerConnection" ).getMethod( "sendPacket", getNMSClass( "Packet" ) ) );
			
			methodCache.put( "setLocation", getNMSClass( "EntityArmorStand" ).getMethod( "setLocation", double.class, double.class, double.class, float.class, float.class ) );
			methodCache.put( "setSmall", getNMSClass( "EntityArmorStand" ).getMethod( "setSmall", boolean.class ) );
			methodCache.put( "setInvisible", getNMSClass( "EntityArmorStand" ).getMethod( "setInvisible", boolean.class ) );
			methodCache.put( "getId", getNMSClass( "EntityArmorStand" ).getMethod( "getId" ) );
			methodCache.put( "getBukkitEntity", getNMSClass( "EntityArmorStand" ).getMethod( "getBukkitEntity" ) );
			methodCache.put( "getArmorStandHandle", getNMSClass( "CraftArmorStand" ).getMethod( "getHandle" ) );
			
			if ( version.equalsIgnoreCase( "v1_8_R3" ) ) {
				methodCache.put( "setNoGravity", getNMSClass( "EntityArmorStand" ).getMethod( "setGravity", boolean.class ) );
				methodCache.put( "getEquipment", getNMSClass( "EntityArmorStand" ).getMethod( "getEquipment", int.class ) );
			} else {
				methodCache.put( "setNoGravity", getNMSClass( "EntityArmorStand" ).getMethod( "setNoGravity", boolean.class ) );
				methodCache.put( "getEquipment", getNMSClass( "EntityArmorStand" ).getMethod( "getEquipment", getNMSClass( "EnumItemSlot" ) ) );
				methodCache.put( "valueOf", getNMSClass( "EnumItemSlot" ).getMethod( "valueOf", String.class ) );
				methodCache.put( "setInvulnerable", getNMSClass( "EntityArmorStand" ).getMethod( "setInvulnerable", boolean.class ) );
				methodCache.put( "setMarker", getNMSClass( "EntityArmorStand" ).getMethod( "setMarker", boolean.class ) );
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}

		constructorCache = new HashMap< Class< ? >, Constructor< ? > >();
		try {
			constructorCache.put( getNMSClass( "EntityArmorStand" ), getNMSClass( "EntityArmorStand" ).getConstructor( getNMSClass( "World" ) ) );
			
			constructorCache.put( getNMSClass( "PacketPlayOutSpawnEntityLiving" ),  getNMSClass( "PacketPlayOutSpawnEntityLiving" ).getConstructor( getNMSClass( "EntityLiving" ) ) );
			if ( version.equalsIgnoreCase( "v1_8_R3" ) ) {
				constructorCache.put( getNMSClass( "PacketPlayOutEntityEquipment" ), getNMSClass( "PacketPlayOutEntityEquipment" ).getConstructor( int.class, int.class, getNMSClass( "ItemStack" ) ) );
			} else {
				constructorCache.put( getNMSClass( "PacketPlayOutEntityEquipment" ), getNMSClass( "PacketPlayOutEntityEquipment" ).getConstructor( int.class, getNMSClass( "EnumItemSlot" ), getNMSClass( "ItemStack" ) ) );
			}
			constructorCache.put( getNMSClass( "PacketPlayOutEntityDestroy" ), getNMSClass( "PacketPlayOutEntityDestroy" ).getConstructor( int[].class ) );
			
			connection = getNMSClass( "EntityPlayer" ).getField( "playerConnection" );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static Method getMethod( String name ) {
		return methodCache.containsKey( name ) ? methodCache.get( name ) : null;
	}

	public static Constructor< ? > getConstructor( Class< ? > clazz ) {
		return constructorCache.containsKey( clazz ) ? constructorCache.get( clazz ) : null;
	}

	public static Class<?> getNMSClass(String name) {
		if ( classCache.containsKey( name ) ) {
			return classCache.get( name );
		}

		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Field getField() {
		return connection;
	}
	
	public static CraftInventoryManager getManager() {
		Object instance = null;
		try {
			instance = containerManager.newInstance();
		} catch ( Exception exception ) {
			Bukkit.getPluginManager().disablePlugin( PublicCrafters.getInstance() );
		}
		
		return ( CraftInventoryManager ) instance; 
	}
	
	public static String getVersion() {
		return version;
	}
}
