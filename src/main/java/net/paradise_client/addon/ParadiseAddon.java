package net.paradise_client.addon;

/**
 * Base class for ParadiseClient addons.
 */
public abstract class ParadiseAddon {
  /**
   * Automatically assigned from fabric.mod.json file.
   */
  public String name;

  /**
   * Automatically assigned from fabric.mod.json file.
   */
  public String[] authors;

  public abstract void onInitialize();
}
