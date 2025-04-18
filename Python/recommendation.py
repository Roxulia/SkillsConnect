import pandas as pd
import numpy as np
import json
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

class Recommend():
    def __init__(self):
        self.category_skills = self.load()

    def load(self):
        with open("category_skills.json", "r") as f:
            loaded_category_skills = json.load(f)

        loaded_category_skills = {k: set(v) for k, v in loaded_category_skills.items()}
        return loaded_category_skills
    
    def recommend_users(self,project_description, project_categories, users, 
                     similarity_weight=0.4, skill_weight=0.4, rating_weight=0.2):
        """
        Recommends the best users for a given project.
        
        Parameters:
        - project_description (str): The description of the project.
        - project_categories (list): List of categories related to the project.
        - users (list): A list of user dictionaries containing:
            - 'id': Unique user ID.
            - 'finished_projects': List of { 'description': str, 'category': str }.
            - 'skills': List of skills.
            - 'rating': Numeric rating of the user.
        - category_skills (dict): Mapping of categories to required skills.
        - similarity_weight (float): Weight for project similarity score.
        - skill_weight (float): Weight for skill matching score.
        - rating_weight (float): Weight for user rating.

        Returns:
        - List of top recommended users sorted by their score.
        """
        
        user_scores = []
        vectorizer = TfidfVectorizer()

        # Compute TF-IDF for the given project description
        project_desc_tfidf = vectorizer.fit_transform([project_description])

        for user in users:
            # 1️⃣ **Cosine Similarity Score** (Between Project & User’s Finished Projects)
            user_projects = [p["detail"] for p in user["finished"]]
            
            if user_projects:
                user_tfidf = vectorizer.transform(user_projects)
                similarity_scores = cosine_similarity(project_desc_tfidf, user_tfidf)
                max_similarity = np.max(similarity_scores)  # Best match
            else:
                max_similarity = 0  # No finished projects

            # 2️⃣ **Skill Match Score**
            user_skills = set(user["skills"])
            required_skills = set()
            for category in project_categories:
                required_skills.update(self.category_skills.get(category, []))

            if required_skills:
                skill_score = len(user_skills & required_skills) / len(required_skills)
            else:
                skill_score = 0  # No category-specific skills

            # 3️⃣ **User Rating (Normalized)**
            normalized_rating = user["rating"] / 5.0  # Assuming rating is out of 5

            # 4️⃣ **Final Weighted Score Calculation**
            final_score = (
                (similarity_weight * max_similarity) + 
                (skill_weight * skill_score) + 
                (rating_weight * normalized_rating)
            )

            user_scores.append({"id": user["id"], "score": float(final_score)})

        # Sort Users by Score (Descending)
        user_scores = sorted(user_scores, key=lambda x: x["score"], reverse=True)

        return user_scores[:3]  # Return Top 3 Users
    
    def recommend_projects(self,projects, user_skills, user_finished_projects, similarity_weight=0.3, skill_weight=0.7, top_n=3):
        """
        Recommend projects based on similarity to user's finished projects and skill match.
        
        Parameters:
        - projects: List of dicts, each with 'description' and 'category'.
        - category_skills: Dict mapping category names to required skills.
        - user_skills: List of user's skills.
        - user_finished_projects: List of dicts with 'description' and 'category'.
        - similarity_weight: Weight for cosine similarity (default 0.6).
        - skill_weight: Weight for skill matching (default 0.4).
        - top_n: Number of top recommendations to return (default 3).
        
        Returns:
        - List of top_n recommended projects.
        """
        
        # ---------------- Step 1: Cosine Similarity Calculation ----------------
        if(not user_finished_projects == []):
            all_descriptions = [proj["detail"] for proj in projects] + [proj["detail"] for proj in user_finished_projects]
            vectorizer = TfidfVectorizer()
            tfidf_matrix = vectorizer.fit_transform(all_descriptions)

            project_similarities = []
            for i, project in enumerate(projects):
                similarity_scores = cosine_similarity(tfidf_matrix[i], tfidf_matrix[len(projects):]).flatten()
                project_similarities.append(max(similarity_scores))  # Max similarity with any user project

            # Normalize similarity scores
            max_sim = max(project_similarities) if max(project_similarities) > 0 else 1
            project_similarities = [sim / max_sim for sim in project_similarities]

            all_category = [proj["category"] for proj in projects] + [proj["category"] for proj in user_finished_projects]
            vectorizer = TfidfVectorizer()
            tfidf_matrix = vectorizer.fit_transform(all_category)

            category_similarities = []
            for i, project in enumerate(projects):
                similarity_scores = cosine_similarity(tfidf_matrix[i], tfidf_matrix[len(projects):]).flatten()
                category_similarities.append(max(similarity_scores))  # Max similarity with any user project

            # Normalize similarity scores
            max_sim = max(category_similarities) if max(category_similarities) > 0 else 1
            category_similarities = [sim / max_sim for sim in category_similarities]
            # ---------------- Step 2: Skill Matching Score ----------------
            skill_match_scores = []
            for project in projects:
                category = project['category']
                required_skills = self.category_skills.get(category, [])
                matching_skills = len(set(required_skills) & set(user_skills))
                
                # Normalize skill match score
                skill_match_score = matching_skills / len(required_skills) if required_skills else 0
                skill_match_scores.append(skill_match_score)

            # ---------------- Step 3: Combine Scores and Sort ----------------
            final_scores = [
                similarity_weight * proj_sim + skill_weight * skill_match + 0.5 * cate
                for proj_sim, skill_match , cate in zip(project_similarities, skill_match_scores,category_similarities)
            ]

            # Sort projects by final score
            sorted_projects = sorted(zip(projects, final_scores), key=lambda x: x[1], reverse=True)
        else:
            # ---------------- Step 2: Skill Matching Score ----------------
            skill_match_scores = []
            for project in projects:
                category = project["category"]
                required_skills = self.category_skills.get(category, [])
                matching_skills = len(set(required_skills) & set(user_skills))
                
                # Normalize skill match score
                skill_match_score = matching_skills / len(required_skills) if required_skills else 0
                skill_match_scores.append(skill_match_score)

            # ---------------- Step 3: Combine Scores and Sort ----------------
            final_scores = [
                skill_weight * skill_match
                for  skill_match in skill_match_scores
            ]

            # Sort projects by final score
            sorted_projects = sorted(zip(projects, final_scores), key=lambda x: x[1], reverse=True)
        # Return top N recommended projects
        return [proj[0] for proj in sorted_projects[:top_n]]