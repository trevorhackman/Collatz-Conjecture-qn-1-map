#!/bin/bash
# Ask Skim to reread each PDF. `open` only activates a PDF that is already open,
# and Skim does not watch for replacement of the file.
osascript - "$@" <<'EOF'
on run argv
  repeat with pdfPath in argv
    set pdfPath to contents of pdfPath
    set theFile to POSIX file pdfPath
    set thePath to POSIX path of (theFile as alias)
    tell application "Skim"
      set theDocs to documents whose path is thePath
      set pageIndex to 0
      if (count of theDocs) > 0 then
        try
          set pageIndex to index of current page of item 1 of theDocs
        end try
        try
          revert theDocs
        end try
      end if
      open theFile
      if pageIndex > 0 then
        set theDocs to documents whose path is thePath
        if (count of theDocs) > 0 then
          try
            go (item 1 of theDocs) to (page pageIndex of item 1 of theDocs)
          end try
        end if
      end if
    end tell
  end repeat
  tell application "Skim" to activate
end run
EOF
