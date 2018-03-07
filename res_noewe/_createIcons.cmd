set RESOLUTION=%1
rem set RESOLUTION=24
set /a STARSIZE=5*RESOLUTION
goto start

:start
svg2png.exe "svg\Cachetype\Ape.svg" "symbols\Ape.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Cito.svg" "symbols\Cito.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Custom.svg" "symbols\Custom.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Earth.svg" "symbols\earth.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Event.svg" "symbols\Event.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Giga.svg" "symbols\Giga.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Lab.svg" "symbols\Lab.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Letterbox.svg" "symbols\Letterbox.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Locless.svg" "symbols\Locless.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Maze.svg" "symbols\Maze.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Megaevent.svg" "symbols\Megaevent.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Multi.svg" "symbols\Multi.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Mystery.svg" "symbols\Mystery.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Tradi.svg" "symbols\Tradi.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Virtual.svg" "symbols\Virtual.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Webcam.svg" "symbols\Webcam.png" %RESOLUTION%
svg2png.exe "svg\Cachetype\Whereigo.svg" "symbols\Whereigo.png" %RESOLUTION%
svg2png.exe "svg\Waypoint\Parking.svg" "symbols\Parking.png" %RESOLUTION%
svg2png.exe "svg\Waypoint\Stage.svg" "symbols\Stage.png" %RESOLUTION%
svg2png.exe "svg\Waypoint\Question.svg" "symbols\Question.png" %RESOLUTION%
svg2png.exe "svg\Waypoint\Final.svg" "symbols\Final.png" %RESOLUTION%
svg2png.exe "svg\Waypoint\Trailhead.svg" "symbols\Trailhead.png" %RESOLUTION%
svg2png.exe "svg\Waypoint\Reference.svg" "symbols\Reference.png" %RESOLUTION%

:button
svg2png.exe "svg\Button\about.svg" "symbols\about.png" %RESOLUTION%
svg2png.exe "svg\Button\add.svg" "symbols\add.png" %RESOLUTION%
svg2png.exe "svg\Button\admin.svg" "symbols\admin.png" %RESOLUTION%
svg2png.exe "svg\Button\apply.svg" "symbols\apply.png" %RESOLUTION%
svg2png.exe "svg\Button\blacklist.svg" "symbols\blacklist.png" %RESOLUTION%
svg2png.exe "svg\Button\bug.svg" "symbols\bug.png" %RESOLUTION%
svg2png.exe "svg\Button\cachetour.svg" "symbols\cachetour.png" %RESOLUTION%
svg2png.exe "svg\Button\calc.svg" "symbols\calc.png" %RESOLUTION%
svg2png.exe "svg\Button\calendar.svg" "symbols\calendar.png" %RESOLUTION%
svg2png.exe "svg\Button\cancel.svg" "symbols\cancel.png" %RESOLUTION%
svg2png.exe "svg\Button\clear.svg" "symbols\clear.png" %RESOLUTION%
svg2png.exe "svg\Button\clnew.svg" "symbols\clnew.png" %RESOLUTION%
svg2png.exe "svg\Button\clopen.svg" "symbols\clopen.png" %RESOLUTION%
svg2png.exe "svg\Button\clsave.svg" "symbols\clsave.png" %RESOLUTION%
svg2png.exe "svg\Button\clsaveas.svg" "symbols\clsaveas.png" %RESOLUTION%
svg2png.exe "svg\Button\compass.svg" "symbols\compass.png" %RESOLUTION%
svg2png.exe "svg\Button\copy.svg" "symbols\copy.png" %RESOLUTION%
svg2png.exe "svg\Button\cw.svg" "symbols\cw.png" %RESOLUTION%
svg2png.exe "svg\Button\database.svg" "symbols\database.png" %RESOLUTION%
svg2png.exe "svg\Button\date_time.svg" "symbols\date_time.png" %RESOLUTION%
svg2png.exe "svg\Button\decode.svg" "symbols\decode.png" %RESOLUTION%
svg2png.exe "svg\Button\degrad.svg" "symbols\degrad.png" %RESOLUTION%
svg2png.exe "svg\Button\delete.svg" "symbols\delete.png" %RESOLUTION%
svg2png.exe "svg\Button\description.svg" "symbols\description.png" %RESOLUTION%
svg2png.exe "svg\Button\details.svg" "symbols\details.png" %RESOLUTION%
svg2png.exe "svg\Button\encode.svg" "symbols\encode.png" %RESOLUTION%
svg2png.exe "svg\Button\examine.svg" "symbols\examine.png" %RESOLUTION%
svg2png.exe "svg\Button\exit.svg" "symbols\exit.png" %RESOLUTION%
svg2png.exe "svg\Button\export.svg" "symbols\export.png" %RESOLUTION%
svg2png.exe "svg\Button\filter.svg" "symbols\filter.png" %RESOLUTION%
svg2png.exe "svg\Button\filterclear.svg" "symbols\filterclear.png" %RESOLUTION%
svg2png.exe "svg\Button\filtercreate.svg" "symbols\filtercreate.png" %RESOLUTION%
svg2png.exe "svg\Button\filterinvert.svg" "symbols\filterinvert.png" %RESOLUTION%
svg2png.exe "svg\Button\filternonselected.svg" "symbols\filternonselected.png" %RESOLUTION%
svg2png.exe "svg\Button\filterselected.svg" "symbols\filterselected.png" %RESOLUTION%
svg2png.exe "svg\Button\from.svg" "symbols\from.png" %RESOLUTION%
svg2png.exe "svg\Button\fromclipboard.svg" "symbols\fromclipboard.png" %RESOLUTION%
svg2png.exe "svg\Button\garmin.svg" "symbols\garmin.png" %RESOLUTION%
svg2png.exe "svg\Button\globe.svg" "symbols\globe.png" %RESOLUTION%
svg2png.exe "svg\Button\goto.svg" "symbols\goto.png" %RESOLUTION%
svg2png.exe "svg\Button\gps.svg" "symbols\gps.png" %RESOLUTION%
svg2png.exe "svg\Button\guiError.svg" "symbols\guiError.png" %RESOLUTION%
svg2png.exe "svg\Button\hint.svg" "symbols\hint.png" %RESOLUTION%
svg2png.exe "svg\Button\home.svg" "symbols\home.png" %RESOLUTION%
svg2png.exe "svg\Button\html.svg" "symbols\html.png" %RESOLUTION%
svg2png.exe "svg\Button\illegal.svg" "symbols\illegal.png" %RESOLUTION%
svg2png.exe "svg\Button\imageadd.svg" "symbols\imageadd.png" %RESOLUTION%
svg2png.exe "svg\Button\images.svg" "symbols\images.png" %RESOLUTION%
svg2png.exe "svg\Button\import.svg" "symbols\import.png" %RESOLUTION%
svg2png.exe "svg\Button\legend.svg" "symbols\legend.png" %RESOLUTION%
svg2png.exe "svg\Button\list.svg" "symbols\list.png" %RESOLUTION%
svg2png.exe "svg\Button\lupe_activated.svg" "symbols\lupe_activated.png" %RESOLUTION%
svg2png.exe "svg\Button\lupe_activated_zin.svg" "symbols\lupe_activated_zin.png" %RESOLUTION%
svg2png.exe "svg\Button\lupe_activated_zout.svg" "symbols\lupe_activated_zout.png" %RESOLUTION%
svg2png.exe "svg\Button\minus.svg" "symbols\minus.png" %RESOLUTION%
svg2png.exe "svg\Button\monitor.svg" "symbols\monitor.png" %RESOLUTION%
svg2png.exe "svg\Button\more.svg" "symbols\more.png" %RESOLUTION%
svg2png.exe "svg\Button\move.svg" "symbols\move.png" %RESOLUTION%
svg2png.exe "svg\Button\newwpt.svg" "symbols\newwpt.png" %RESOLUTION%
svg2png.exe "svg\Button\next.svg" "symbols\next.png" %RESOLUTION%
svg2png.exe "svg\Button\nosort.svg" "symbols\nosort.png" %RESOLUTION%
svg2png.exe "svg\Button\notes.svg" "symbols\notes.png" %RESOLUTION%
svg2png.exe "svg\Button\ok.svg" "symbols\ok.png" %RESOLUTION%
svg2png.exe "svg\Button\person.svg" "symbols\person.png" %RESOLUTION%
svg2png.exe "svg\Button\plus.svg" "symbols\plus.png" %RESOLUTION%
svg2png.exe "svg\Button\previous.svg" "symbols\previous.png" %RESOLUTION%
svg2png.exe "svg\Button\profile.svg" "symbols\profile.png" %RESOLUTION%
svg2png.exe "svg\Button\projection.svg" "symbols\projection.png" %RESOLUTION%
svg2png.exe "svg\Button\radar.svg" "symbols\radar.png" %RESOLUTION%
svg2png.exe "svg\Button\search.svg" "symbols\search.png" %RESOLUTION%
svg2png.exe "svg\Button\searchmore.svg" "symbols\searchmore.png" %RESOLUTION%
svg2png.exe "svg\Button\searchoff.svg" "symbols\searchoff.png" %RESOLUTION%
svg2png.exe "svg\Button\snap2gps.svg" "symbols\snap2gps.png" %RESOLUTION%
svg2png.exe "svg\Button\solver.svg" "symbols\solver.png" %RESOLUTION%
svg2png.exe "svg\Button\sort.svg" "symbols\sort.png" %RESOLUTION%
svg2png.exe "svg\Button\system.svg" "symbols\system.png" %RESOLUTION%
svg2png.exe "svg\Button\tabs.svg" "symbols\tabs.png" %RESOLUTION%
svg2png.exe "svg\Button\text.svg" "symbols\text.png" %RESOLUTION%
svg2png.exe "svg\Button\toclipboard.svg" "symbols\toclipboard.png" %RESOLUTION%
svg2png.exe "svg\Button\toggle.svg" "symbols\toggle.png" %RESOLUTION%
svg2png.exe "svg\Button\tools.svg" "symbols\tools.png" %RESOLUTION%
svg2png.exe "svg\Button\trash.svg" "symbols\trash.png" %RESOLUTION%
svg2png.exe "svg\Button\version.svg" "symbols\version.png" %RESOLUTION%
svg2png.exe "svg\Button\waypoint.svg" "symbols\waypoint.png" %RESOLUTION%
svg2png.exe "svg\Button\whitelist.svg" "symbols\whitelist.png" %RESOLUTION%
svg2png.exe "svg\Button\wolflanguage.svg" "symbols\wolflanguage.png" %RESOLUTION%
svg2png.exe "svg\Button\zoom1to1.svg" "symbols\zoom1to1.png" %RESOLUTION%
svg2png.exe "svg\Button\position_yellow.svg" "symbols\position_yellow.png" %RESOLUTION%
svg2png.exe "svg\Button\position_red.svg" "symbols\position_red.png" %RESOLUTION%
svg2png.exe "svg\Button\position_green.svg" "symbols\position_green.png" %RESOLUTION%
svg2png.exe "svg\Button\no.svg" "symbols\no.png" %RESOLUTION%

:size
svg2png.exe "svg\Size\sizeLarge.svg" "symbols\sizeLarge.png" %RESOLUTION%
svg2png.exe "svg\Size\sizeMicro.svg" "symbols\sizeMicro.png" %RESOLUTION%
svg2png.exe "svg\Size\sizeNotChosen.svg" "symbols\sizeNotChosen.png" %RESOLUTION%
svg2png.exe "svg\Size\sizeReg.svg" "symbols\sizeReg.png" %RESOLUTION%
svg2png.exe "svg\Size\sizeSmall.svg" "symbols\sizeSmall.png" %RESOLUTION%
svg2png.exe "svg\Size\sizeVirtual.svg" "symbols\sizeVirtual.png" %RESOLUTION%
svg2png.exe "svg\Size\sizeVLarge.svg" "symbols\sizeVLarge.png" %RESOLUTION%
svg2png.exe "svg\Size\sizeOther.svg" "symbols\sizeOther.png" %RESOLUTION%

:star
svg2png.exe "svg\Star\star0.svg" "symbols\star0.png" %STARSIZE%
svg2png.exe "svg\Star\star10.svg" "symbols\star10.png" %STARSIZE%
svg2png.exe "svg\Star\star15.svg" "symbols\star15.png" %STARSIZE%
svg2png.exe "svg\Star\star20.svg" "symbols\star20.png" %STARSIZE%
svg2png.exe "svg\Star\star25.svg" "symbols\star5.png" %STARSIZE%
svg2png.exe "svg\Star\star30.svg" "symbols\star30.png" %STARSIZE%
svg2png.exe "svg\Star\star35.svg" "symbols\star35.png" %STARSIZE%
svg2png.exe "svg\Star\star40.svg" "symbols\star40.png" %STARSIZE%
svg2png.exe "svg\Star\star45.svg" "symbols\star45.png" %STARSIZE%
svg2png.exe "svg\Star\star50.svg" "symbols\star50.png" %STARSIZE%

pause
goto exit
exit


