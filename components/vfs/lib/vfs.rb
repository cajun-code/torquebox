
require 'java'

module VFS
end

require 'vfs/file'
require 'vfs/dir'
require 'vfs/glob_filter'
require 'vfs/ext/vfs'
require 'vfs/ext/io'
require 'vfs/ext/file'
require 'vfs/ext/file_test'
require 'vfs/ext/dir'
require 'vfs/ext/pathname'
require 'vfs/ext/kernel'
require 'vfs/ext/jdbc'


module ::VFS
  def self.resolve_within_archive(path)
    path = path.to_s
    return path if ( path =~ %r(^vfs:) )
    cur = path
    while ( cur != '.' && cur != '/' )
      if ( ::File.exist_without_vfs?( cur ) )

        child_path = path[cur.length..-1]

        if ( cur[-1,1] == '/' )
          cur = cur[0..-2]
        end
        return ::VFS.resolve_path_url( cur ), child_path
      end
      cur = ::File.dirname( cur ) + '/'
    end
    nil
  end

  def self.resolve_path_url(path)
    prefix = case
             when path =~ /^\//            # unix absolute
               "vfs:"
             when path =~ /^[[:alpha:]]:/  # windows absolute
               "vfs:/"
             else
               "vfs:#{::Dir.pwd}/"         # relative
             end
    "#{prefix}#{path}"
  end

end
