param(
    [ValidateSet('all', 'test', 'demo', 'benchmark', 'plots')]
    [string]$Task = 'demo'
)
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

function Check-Exit([string]$Step) {
    if ($LASTEXITCODE -ne 0) { throw "$Step failed (exit $LASTEXITCODE)." }
}

if ($Task -ne 'plots') {
    $jdk = [Environment]::GetEnvironmentVariable('JAVA_HOME', 'User')
    if (!$jdk) { $jdk = $env:JAVA_HOME }
    if ($jdk -and (Test-Path "$jdk/bin/javac.exe")) {
        $compiler = "$jdk/bin/javac.exe"
        $runtime = "$jdk/bin/java.exe"
    } else {
        $compiler = (Get-Command javac -ErrorAction Stop).Source
        $runtime = Join-Path (Split-Path $compiler) 'java.exe'
    }
    New-Item -ItemType Directory -Force 'target/classes' | Out-Null
    $sources = @(Get-ChildItem 'src/main/java' -Filter '*.java' -Recurse | ForEach-Object FullName)
    & $compiler -encoding UTF-8 --release 17 -d 'target/classes' $sources
    Check-Exit 'Compilation'
    $tasks = if ($Task -eq 'all') { @('test', 'demo', 'benchmark') } else { @($Task) }
    foreach ($action in $tasks) {
        & $runtime -cp 'target/classes' daa.assignment4.Main "--$action"
        Check-Exit $action
    }
}

if ($Task -eq 'plots' -or $Task -eq 'all') {
    $python = Join-Path $env:LOCALAPPDATA 'Programs/Python/Python313/python.exe'
    if (!(Test-Path $python)) { $python = (Get-Command python -ErrorAction Stop).Source }
    if (!(Test-Path '.venv/Scripts/python.exe')) {
        & $python -m venv .venv
        Check-Exit 'Virtual environment creation'
    }
    & '.venv/Scripts/python.exe' -m pip install -r requirements-plot.txt
    Check-Exit 'Plot dependencies'
    & '.venv/Scripts/python.exe' scripts/plot_results.py
    Check-Exit 'Plot rendering'
}
