package pl.asie.foamfix;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import net.minecraftforge.fml.loading.FMLPaths;

public enum FoamyConfig {
	TRIM_LISTS("features", "trim-lists") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("true");
			setComment("Enable the trimming of collections to save memory", "Impact scales with the number of models");
		}
	},
	STATES("features", "better-states") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("true");
			setComment("Enable the replacement of BlockStates to save memory", "Large impact which scales with the number blockstates");
		}
	},
	POOL_MULTIPART_PREDICATES("features", "multipart-models", "pool") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("true");
			setComment("Enable the caching of multipart model predicates to save memory", "Impact heavily scales with the number of similar multipart models");
		}
	},
	BETTER_MULTIPART("features", "multipart-models", "better") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("true");
			setComment("Enable the improvement of baked multipart models to save memory", "Impact scales with the number of complex multipart models");
		}
	},
	REPLACE_MULTIPART("features", "multipart-models", "resolve") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("true");
			setComment("Enable the resolving of multipart models by blockstate", "Trades higher object count for simpler objects", "Reduces most memory of the three options");
		}
	},
	RECYCLE_IDENTIFIERS("features", "recycle-resource-locations") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("true");
			setComment("Enable the recycling of resource locations rather than making new ones", "Reduces memory use where they are indefinitely retained");
		}		
	},
	CULL_DFU("features", "dfu") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("true");
			setComment("Enable the removal of DataFixerUpper", "Reduces startup times and memory use, at the expense of not being able to update worlds");
		}		
	},
	THREAD_MODELS("features", "experimental", "thread-models") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("false");
			setComment("Enable loading models in parallel to speed up startup", "Potentially could cause incompatibilities with custom models");
		}
	},
	CACHE_MODELS("features", "experimental", "cache-models") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("false");
			setComment("Enable the caching of models after the first load to speed up startup", "Will either work very well or cause bad incompatibilities with custom models");
		}
	},
	LOG_SURFACE_MODELS("features", "experimental", "cache-models", "debug", "log-surface-models") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("false");
			setComment("Enable the logging of primary model types");
		}
	},
	LOG_ALL_MODELS("features", "experimental", "cache-models", "debug", "log-all-models") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("false");
			setComment("Enable the logging of model types recursively");
		}
	},
	LOG_MODELS_BLAME("features", "experimental", "cache-models", "debug", "log-model-nonserialisability") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("false");
			setComment("Enable the logging of why a given model isn't serialisable");
		}
	},
	COMPARE_CACHED_MODELS("features", "experimental", "cache-models", "debug", "compare") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("true");
			setComment("Enable the comparing of newly cached models to the originals to observe any differences");
		}
	},
	LOG_DFU("debug", "dfu") {
		@Override
		boolean isValid(String value) {
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}

		@Override
		void fillDefault() {
			set("false");
			setComment("Enable the logging of missed NBT updates from DataFixerUpper being disabled", "Has no impact if features/dfu is disabled");
		}
	};

	final String section, config;

	private FoamyConfig(String... path) {
		int end = path.length - 1;
		if (end < 1) throw new IllegalArgumentException("Missing section for " + this);

		section = String.join("/", Arrays.copyOf(path, end));
		config = path[end];
	}

	private boolean isValid() {
		Section section = CONFIG.get(this.section);
		if (section == null) return false;

		return isValid(section.get(config));
	}

	abstract boolean isValid(String value);

	final void set(String value) {
		CONFIG.put(section, config, value);
	}

	final void setComment(String... comment) {
		CONFIG.get(section).putComment(config, String.join(CONFIG.getConfig().getLineSeparator(), comment));
	}

	abstract void fillDefault();

	public boolean asBoolean() {
		return "true".equalsIgnoreCase(CONFIG.get(section, config));
	}

	private static final Ini CONFIG = new Ini();
	static {
		Config settings = Config.getGlobal().clone();
		CONFIG.setConfig(settings);
		settings.setTree(true);

		Path config = FMLPaths.CONFIGDIR.get().resolve("foamfix.ini");
		CONFIG.setFile(config.toFile());

		out: if (Files.exists(config)) {
			try (Reader in = Files.newBufferedReader(config)) {
				CONFIG.load(in);
			} catch (IOException e) {
				if (e instanceof InvalidFileFormatException) {
					try {
						Files.delete(config);
						break out;
					} catch (IOException again) {
						e.addSuppressed(again);
					}
				}

				throw new RuntimeException("Error reading FoamFix config file at " + config, e);
			}
		}

		boolean needsWrite = false;
		for (FoamyConfig option : values()) {
			if (!option.isValid()) {
				needsWrite = true;
				option.fillDefault();
			}
		}

		if (needsWrite) {
			try (Writer out = Files.newBufferedWriter(config)) {
				CONFIG.store(out);
			} catch (IOException e) {
				throw new RuntimeException("Error writing FoamFix config file to " + config, e);
			}
		}
	}
}