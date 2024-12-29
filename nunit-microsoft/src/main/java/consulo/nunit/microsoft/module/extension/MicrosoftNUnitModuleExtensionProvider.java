package consulo.nunit.microsoft.module.extension;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.nunit.icon.NUnitIconGroup;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11-Sep-22
 */
@ExtensionImpl
public class MicrosoftNUnitModuleExtensionProvider implements ModuleExtensionProvider<MicrosoftNUnitModuleExtension>
{
	@Nonnull
	@Override
	public String getId()
	{
		return "microsoft-nunit";
	}

	@Nullable
	@Override
	public String getParentId()
	{
		return "microsoft-dotnet";
	}

	@Nonnull
	@Override
	public LocalizeValue getName()
	{
		return LocalizeValue.localizeTODO("NUnit");
	}

	@Nonnull
	@Override
	public Image getIcon()
	{
		return NUnitIconGroup.nunit();
	}

	@Nonnull
	@Override
	public ModuleExtension<MicrosoftNUnitModuleExtension> createImmutableExtension(@Nonnull ModuleRootLayer moduleRootLayer)
	{
		return new MicrosoftNUnitModuleExtension(getId(), moduleRootLayer);
	}

	@Nonnull
	@Override
	public MutableModuleExtension<MicrosoftNUnitModuleExtension> createMutableExtension(@Nonnull ModuleRootLayer moduleRootLayer)
	{
		return new MicrosoftNUnitMutableModuleExtension(getId(), moduleRootLayer);
	}
}
