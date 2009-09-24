
class Dir

  class << self

    #alias_method :open_before_vfs, :open
    alias_method :glob_before_vfs, :glob

    def open(str)
      result = dir = VFS::Dir.new( str )
      if block_given?
        begin
          result = yield( dir )
        ensure
          dir.close 
        end
      end
      result
    end

    def [](pattern)
      self.glob( pattern )
    end

    def glob(pattern,flags=nil)

      first_special = ( pattern =~ /[\*\?\[\{]/ )
      base          = pattern[0, first_special]

      if ( File.exist?( base ) && File.directory?( base ) )
        return glob_before_vfs( pattern )
      end

      unless ( base =~ %r(^vfs[^:]+) )
        existing = VFS.first_existing( base )
        return [] unless existing
        is_archive = Java::OrgJbossVirtualPluginsContextJar::JarUtils.isArchive( File.basename( existing ) )
        if ( is_archive )
          base = "vfszip://#{Dir.pwd}/#{existing}"
          matcher = pattern[ existing.length+1..-1 ]
        end
      else
        matcher = pattern[first_special..-1]
      end
      root = org.jboss.virtual.VFS.root( base[0..-1] )
      root.children_recursively( VFS::GlobFilter.new( matcher ) ).collect{|e| "#{base}#{e.path_name}"}
    end

  end

  class VFSDir
    attr_reader :path
    attr_reader :pos
    alias_method :tell, :pos

    def initialize(path)
      @path         = path
      @virtual_file = org.jboss.virtual.VFS.root( path )
      @pos          = 0
      @closed       = false
    end

    def close
      @closed = true
    end

    def each
      @virtual_file.children.each do |child|
        yield child.name
      end
    end

    def rewind
      @pos = 0
    end

    def read
      children = @virtual_file.children
      return nil unless ( @pos < children.size )
      child = children[@pos]
      @pos += 1
      child.name
    end
    
    def seek(i)
      @pos = i
      self
    end

    def pos=(i)
      @pos = i
    end

  end

end 
