import matplotlib.pyplot as plt
import numpy as np

metrics = ['Precision', 'Recall', 'F1 Score', 'Accuracy']
phobert_scores = [0.4483, 1.0000, 0.6190, 0.9490]
tfidf_scores = [0.8532, 0.8493, 0.8497, 0.8493]

x = np.arange(len(metrics))
width = 0.35

fig, ax = plt.subplots(figsize=(10, 6))
rects1 = ax.bar(x - width/2, phobert_scores, width, label='PhoBERT')
rects2 = ax.bar(x + width/2, tfidf_scores, width, label='TF-IDF')

ax.set_ylabel('Scores')
ax.set_title('Comparison of PhoBERT vs TF-IDF Performance')
ax.set_xticks(x)
ax.set_xticklabels(metrics)
ax.legend()

def autolabel(rects):
    for rect in rects:
        height = rect.get_height()
        ax.annotate(f'{height:.4f}',
                    xy=(rect.get_x() + rect.get_width() / 2, height),
                    xytext=(0, 3),
                    textcoords="offset points",
                    ha='center', va='bottom', rotation=90)

autolabel(rects1)
autolabel(rects2)

plt.tight_layout()
plt.show()