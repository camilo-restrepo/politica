from flask import Flask
import  numpy
from flask import request
from flask import jsonify
from array import *
from sklearn.ensemble import RandomForestClassifier
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.externals import joblib

def load(filePath):
  return joblib.load(filePath)

app = Flask(__name__)
vectorized = load("model/vectorized.pkl")
forest = load("model/forest.pkl")

@app.route("/predict", methods=["POST"])
def predict():
    tweetTextList = [request.data]
    trainingDataFeatures = vectorized.transform(tweetTextList)
    trainingDataFeatures = trainingDataFeatures.toarray()
    prediction = forest.predict(trainingDataFeatures)
    return jsonify({"result": str(prediction[0])})

if __name__ == "__main__":
    app.run(debug=False)
