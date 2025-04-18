from flask import Flask, request, jsonify
from flask_cors import CORS
import json
from recommendation import Recommend

app = Flask(__name__)
CORS(app)
recommendation = Recommend()

@app.route('/project', methods=['POST'])
def project():
    try:
        # Extract JSON data from the request
        data = request.json
        skills = data['skills']
        finished = data["finished"]
        projects = data["projects"]

        rec_list = recommendation.recommend_projects(projects,skills,finished)
        result = [p['id'] for p in rec_list]

        return jsonify({'result': result})
    except Exception as e:
        return jsonify({'error': str(e)}), 400
    
@app.route('/bidder', methods=['POST'])
def bidder():
    try:
        # Extract JSON data from the request
        data = request.json
        description = data['description']
        category = data["category"]
        users = data["users"]

        rec_list = recommendation.recommend_users(description,category,users)
        result = [p['id'] for p in rec_list]

        return jsonify({'result': result})
    except Exception as e:
        return jsonify({'error': str(e)}), 400


if __name__ == '__main__':
    app.run(debug=True)