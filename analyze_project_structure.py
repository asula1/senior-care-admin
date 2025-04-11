#!/usr/bin/env python3
import os
import json
from collections import defaultdict

def analyze_directory(path, max_depth=None, current_depth=0, exclude_dirs=None):
    """
    Recursively analyze a directory structure and return a dictionary representation.
    
    Args:
        path (str): The path to analyze
        max_depth (int, optional): Maximum depth to traverse
        current_depth (int): Current recursion depth
        exclude_dirs (list, optional): List of directory names to exclude
        
    Returns:
        dict: A dictionary representing the directory structure
    """
    if exclude_dirs is None:
        exclude_dirs = ['node_modules', '.git', '__pycache__', 'build', 'gradle']
    
    if max_depth is not None and current_depth > max_depth:
        return "..."
    
    result = {}
    
    try:
        for item in os.listdir(path):
            if item.startswith('.'):
                continue
                
            item_path = os.path.join(path, item)
            
            if os.path.isdir(item_path):
                if item in exclude_dirs:
                    result[item] = "... (excluded)"
                else:
                    result[item] = analyze_directory(
                        item_path, 
                        max_depth, 
                        current_depth + 1, 
                        exclude_dirs
                    )
            else:
                result[item] = os.path.getsize(item_path)  # Store file size
    except Exception as e:
        return f"Error: {str(e)}"
    
    return result

def count_files_by_extension(structure, counts=None, path=""):
    """
    Count files by extension in the structure.
    
    Args:
        structure (dict): The directory structure
        counts (dict, optional): Dictionary to store counts
        path (str): Current path for reporting
        
    Returns:
        dict: A dictionary with counts by extension
    """
    if counts is None:
        counts = defaultdict(int)
    
    for name, content in structure.items():
        current_path = os.path.join(path, name)
        
        if isinstance(content, dict):
            count_files_by_extension(content, counts, current_path)
        elif isinstance(content, (int, float)):  # It's a file (size)
            ext = os.path.splitext(name)[1].lower()
            if ext:
                counts[ext] += 1
            else:
                counts["(no extension)"] += 1
    
    return counts

def analyze_component_relationships(structure):
    """
    Analyze relationships between components based on imports and dependencies.
    This is a simplified version that just identifies components.
    
    Args:
        structure (dict): The directory structure
        
    Returns:
        dict: A dictionary with component information
    """
    components = {
        "backend": {
            "type": "Spring Boot API",
            "language": "Java",
            "path": "backend"
        },
        "admin-web": {
            "type": "React Web Application",
            "language": "JavaScript",
            "path": "admin-web"
        },
        "watch-app": {
            "type": "Android Wear OS Application",
            "language": "Kotlin",
            "path": "watch-app"
        },
        "mobile-app": {
            "type": "Mobile Application",
            "language": "Unknown",
            "path": "mobile-app",
            "status": "Empty directory"
        }
    }
    
    return components

def main():
    base_path = os.path.dirname(os.path.abspath(__file__))
    print(f"Analyzing project structure at: {base_path}")
    
    # Analyze with limited depth for overview
    structure = analyze_directory(base_path, max_depth=3)
    
    # Count files by extension
    extension_counts = count_files_by_extension(structure)
    
    # Analyze component relationships
    components = analyze_component_relationships(structure)
    
    # Create a report
    report = {
        "project_name": "Senior Care Watch System",
        "components": components,
        "file_extensions": dict(extension_counts),
        "structure_overview": structure
    }
    
    # Print the report
    print("\n=== Senior Care Watch Project Analysis ===\n")
    
    print("Components:")
    for name, info in report["components"].items():
        print(f"  - {name}: {info['type']} ({info['language']})")
        if "status" in info:
            print(f"    Status: {info['status']}")
    
    print("\nFile Extensions:")
    for ext, count in sorted(report["file_extensions"].items(), key=lambda x: x[1], reverse=True):
        print(f"  - {ext}: {count} files")
    
    # Save the full report to a JSON file
    with open(os.path.join(base_path, "project_analysis.json"), "w") as f:
        json.dump(report, f, indent=2)
    
    print("\nFull analysis saved to project_analysis.json")

if __name__ == "__main__":
    main()