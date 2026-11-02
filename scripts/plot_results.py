"""Render timing plots from the saved benchmark CSV."""

import csv
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parents[1]


def main():
    with (ROOT / "results/results.csv").open(newline="", encoding="utf-8") as stream:
        rows = list(csv.DictReader(stream))
    plt.rcParams.update({"font.family": "DejaVu Sans", "font.size": 10,
                         "svg.fonttype": "none", "svg.hashsalt": "daa-assignment4"})
    output = ROOT / "results/plots"
    output.mkdir(parents=True, exist_ok=True)

    def series(ax, algorithm, workload, sizes, color, marker):
        values = sorted((r for r in rows if r["algorithm"] == algorithm
                         and r["workload"] == workload), key=lambda r: int(r["size"]))
        if [int(r["size"]) for r in values] != sizes:
            raise ValueError(f"Missing or duplicated sizes for {algorithm}/{workload}")
        ax.plot(sizes, [float(r["mean_ns"]) / 1_000_000 for r in values],
                label=algorithm, color=color, marker=marker, linewidth=2)

    def style(ax, title, xlabel, sizes):
        ax.set_title(title, fontweight="bold")
        ax.set_xlabel(xlabel)
        ax.set_ylabel("Mean execution time (ms)")
        ax.set_xticks(sizes, [f"{size:,}" for size in sizes])
        ax.set_ylim(bottom=0)
        ax.grid(axis="y", color="#d7dde5")
        ax.spines[["top", "right"]].set_visible(False)
        ax.legend(frameon=False)

    fig, ax = plt.subplots(figsize=(8, 5), constrained_layout=True)
    sizes = [100, 500, 1000]
    series(ax, "LCS", "random", sizes, "#315c99", "o")
    series(ax, "Edit Distance", "random", sizes, "#d77b30", "s")
    style(ax, "Dynamic programming: mean of 5 runs", "Length of each input string", sizes)
    fig.savefig(output / "dp_time.svg", metadata={"Date": None})
    plt.close(fig)

    fig, axes = plt.subplots(1, 2, figsize=(12, 5), constrained_layout=True, sharey=True)
    sizes = [10000, 50000, 100000]
    for ax, workload in zip(axes, ("present", "absent")):
        for algorithm, color, marker in (("Naive", "#315c99", "o"),
                                          ("KMP", "#d77b30", "s"),
                                          ("Rabin-Karp", "#188a79", "^")):
            series(ax, algorithm, workload, sizes, color, marker)
        style(ax, f"Pattern {workload}", "Text length", sizes)
    fig.suptitle("String matching: mean of 5 runs, pattern length 16", fontweight="bold")
    fig.savefig(output / "matching_time.svg", metadata={"Date": None})
    plt.close(fig)
    for svg in output.glob("*.svg"):
        content = svg.read_text(encoding="utf-8")
        svg.write_text("\n".join(line.rstrip() for line in content.splitlines()) + "\n",
                       encoding="utf-8")
    print(f"Wrote timing plots to {output}")


if __name__ == "__main__":
    main()
