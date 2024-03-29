package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAnonymousClass;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLClassMatcher;
import gr.uom.java.xmi.UMLGeneralization;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.UMLRealization;
import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.decomposition.AbstractCodeMapping;
import gr.uom.java.xmi.decomposition.OperationInvocation;
import gr.uom.java.xmi.decomposition.StatementObject;
import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.decomposition.replacement.Replacement;
import gr.uom.java.xmi.decomposition.replacement.Replacement.ReplacementType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.util.PrefixSuffixUtils;

public class UMLModelDiff {
   private static final int MAXIMUM_NUMBER_OF_COMPARED_METHODS = 100;
   private List<UMLClass> addedClasses;
   private List<UMLClass> removedClasses;
   
   private List<UMLGeneralization> addedGeneralizations;
   private List<UMLGeneralization> removedGeneralizations;
   private List<UMLGeneralizationDiff> generalizationDiffList;
   private List<UMLRealization> addedRealizations;
   private List<UMLRealization> removedRealizations;
   private List<UMLRealizationDiff> realizationDiffList;
   
   private List<UMLClassDiff> commonClassDiffList;
   private List<UMLClassMoveDiff> classMoveDiffList;
   private List<UMLClassMoveDiff> innerClassMoveDiffList;
   private List<UMLClassRenameDiff> classRenameDiffList;
   private List<Refactoring> refactorings;
   private Set<String> deletedFolderPaths;
   
   public UMLModelDiff() {
      this.addedClasses = new ArrayList<UMLClass>();
      this.removedClasses = new ArrayList<UMLClass>();
      this.addedGeneralizations = new ArrayList<UMLGeneralization>();
      this.removedGeneralizations = new ArrayList<UMLGeneralization>();
      this.generalizationDiffList = new ArrayList<UMLGeneralizationDiff>();
      this.realizationDiffList = new ArrayList<UMLRealizationDiff>();
      this.addedRealizations = new ArrayList<UMLRealization>();
      this.removedRealizations = new ArrayList<UMLRealization>();
      this.commonClassDiffList = new ArrayList<UMLClassDiff>();
      this.classMoveDiffList = new ArrayList<UMLClassMoveDiff>();
      this.innerClassMoveDiffList = new ArrayList<UMLClassMoveDiff>();
      this.classRenameDiffList = new ArrayList<UMLClassRenameDiff>();
      this.refactorings = new ArrayList<Refactoring>();
      this.deletedFolderPaths = new LinkedHashSet<String>();
   }

   public void reportAddedClass(UMLClass umlClass) {
	   if(!addedClasses.contains(umlClass))
		   this.addedClasses.add(umlClass);
   }

   public void reportRemovedClass(UMLClass umlClass) {
	   if(!removedClasses.contains(umlClass))
		   this.removedClasses.add(umlClass);
   }

   public void reportAddedGeneralization(UMLGeneralization umlGeneralization) {
      this.addedGeneralizations.add(umlGeneralization);
   }

   public void reportRemovedGeneralization(UMLGeneralization umlGeneralization) {
      this.removedGeneralizations.add(umlGeneralization);
   }

   public void reportAddedRealization(UMLRealization umlRealization) {
      this.addedRealizations.add(umlRealization);
   }

   public void reportRemovedRealization(UMLRealization umlRealization) {
      this.removedRealizations.add(umlRealization);
   }

   public void addUMLClassDiff(UMLClassDiff classDiff) {
      this.commonClassDiffList.add(classDiff);
   }

   private UMLClassBaseDiff getUMLClassDiff(String className) {
      for(UMLClassDiff classDiff : commonClassDiffList) {
         if(classDiff.matches(className))
            return classDiff;
      }
      for(UMLClassMoveDiff classDiff : classMoveDiffList) {
         if(classDiff.matches(className))
            return classDiff;
      }
      for(UMLClassMoveDiff classDiff : innerClassMoveDiffList) {
         if(classDiff.matches(className))
            return classDiff;
      }
      for(UMLClassRenameDiff classDiff : classRenameDiffList) {
         if(classDiff.matches(className))
            return classDiff;
      }
      return null;
   }

   private UMLClassBaseDiff getUMLClassDiff(UMLType type) {
      for(UMLClassDiff classDiff : commonClassDiffList) {
         if(classDiff.matches(type))
            return classDiff;
      }
      for(UMLClassMoveDiff classDiff : classMoveDiffList) {
         if(classDiff.matches(type))
            return classDiff;
      }
      for(UMLClassMoveDiff classDiff : innerClassMoveDiffList) {
         if(classDiff.matches(type))
            return classDiff;
      }
      for(UMLClassRenameDiff classDiff : classRenameDiffList) {
         if(classDiff.matches(type))
            return classDiff;
      }
      return null;
   }

   private UMLClassBaseDiff getUMLClassDiffWithAttribute(Replacement pattern) {
      for(UMLClassDiff classDiff : commonClassDiffList) {
         if(classDiff.findAttributeInOriginalClass(pattern.getBefore()) != null &&
        		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
            return classDiff;
      }
      for(UMLClassMoveDiff classDiff : classMoveDiffList) {
         if(classDiff.findAttributeInOriginalClass(pattern.getBefore()) != null &&
        		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
            return classDiff;
      }
      for(UMLClassMoveDiff classDiff : innerClassMoveDiffList) {
         if(classDiff.findAttributeInOriginalClass(pattern.getBefore()) != null &&
        		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
            return classDiff;
      }
      for(UMLClassRenameDiff classDiff : classRenameDiffList) {
         if(classDiff.findAttributeInOriginalClass(pattern.getBefore()) != null &&
        		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
            return classDiff;
      }
      return null;
   }

   private UMLClassBaseDiff getUMLClassDiffWithExistingAttributeAfter(Replacement pattern) {
      for(UMLClassDiff classDiff : commonClassDiffList) {
         if(classDiff.findAttributeInOriginalClass(pattern.getAfter()) != null &&
        		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
            return classDiff;
      }
      for(UMLClassMoveDiff classDiff : classMoveDiffList) {
         if(classDiff.findAttributeInOriginalClass(pattern.getAfter()) != null &&
        		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
            return classDiff;
      }
      for(UMLClassMoveDiff classDiff : innerClassMoveDiffList) {
         if(classDiff.findAttributeInOriginalClass(pattern.getAfter()) != null &&
        		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
            return classDiff;
      }
      for(UMLClassRenameDiff classDiff : classRenameDiffList) {
         if(classDiff.findAttributeInOriginalClass(pattern.getAfter()) != null &&
        		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
            return classDiff;
      }
      return null;
   }

   private UMLClassBaseDiff getUMLClassDiffWithNewAttributeAfter(Replacement pattern) {
      for(UMLClassDiff classDiff : commonClassDiffList) {
          if(classDiff.findAttributeInOriginalClass(pattern.getAfter()) == null &&
         		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
             return classDiff;
       }
       for(UMLClassMoveDiff classDiff : classMoveDiffList) {
          if(classDiff.findAttributeInOriginalClass(pattern.getAfter()) == null &&
         		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
             return classDiff;
       }
       for(UMLClassMoveDiff classDiff : innerClassMoveDiffList) {
          if(classDiff.findAttributeInOriginalClass(pattern.getAfter()) == null &&
         		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
             return classDiff;
       }
       for(UMLClassRenameDiff classDiff : classRenameDiffList) {
          if(classDiff.findAttributeInOriginalClass(pattern.getAfter()) == null &&
         		 classDiff.findAttributeInNextClass(pattern.getAfter()) != null)
             return classDiff;
       }
       return null;
   }

   public boolean isSubclassOf(String subclass, String finalSuperclass) {
	   return isSubclassOf(subclass, finalSuperclass, new LinkedHashSet<String>());
   }

   private boolean isSubclassOf(String subclass, String finalSuperclass, Set<String> visitedClasses) {
	   if(visitedClasses.contains(subclass)) {
		   return false;
	   }
	   else {
		   visitedClasses.add(subclass);
	   }
	   UMLClassBaseDiff subclassDiff = getUMLClassDiff(subclass);
	   if(subclassDiff == null) {
		   subclassDiff = getUMLClassDiff(UMLType.extractTypeObject(subclass));
	   }
	   if(subclassDiff != null) {
		   UMLType superclass = subclassDiff.getSuperclass();
		   if(superclass != null) {
			   return checkInheritanceRelationship(superclass, finalSuperclass, visitedClasses);
		   }
		   else if(subclassDiff.getOldSuperclass() != null && subclassDiff.getNewSuperclass() != null &&
				   !subclassDiff.getOldSuperclass().equals(subclassDiff.getNewSuperclass()) && looksLikeAddedClass(subclassDiff.getNewSuperclass()) != null) {
			   UMLClass addedClass = looksLikeAddedClass(subclassDiff.getNewSuperclass());
			   if(addedClass.getSuperclass() != null) {
				   return checkInheritanceRelationship(addedClass.getSuperclass(), finalSuperclass, visitedClasses);
			   }
		   }
		   else if(subclassDiff.getOldSuperclass() == null && subclassDiff.getNewSuperclass() != null && looksLikeAddedClass(subclassDiff.getNewSuperclass()) != null) {
			   UMLClass addedClass = looksLikeAddedClass(subclassDiff.getNewSuperclass());
			   return checkInheritanceRelationship(UMLType.extractTypeObject(addedClass.getName()), finalSuperclass, visitedClasses);
		   }
		   for(UMLType implementedInterface : subclassDiff.getAddedImplementedInterfaces()) {
			   if(checkInheritanceRelationship(implementedInterface, finalSuperclass, visitedClasses)) {
				   return true;
			   }
		   }
	   }
	   UMLClass addedClass = getAddedClass(subclass);
	   if(addedClass == null) {
		   addedClass = looksLikeAddedClass(UMLType.extractTypeObject(subclass));
	   }
	   if(addedClass != null) {
		   UMLType superclass = addedClass.getSuperclass();
		   if(superclass != null) {
			   return checkInheritanceRelationship(superclass, finalSuperclass, visitedClasses);
		   }
		   for(UMLType implementedInterface : addedClass.getImplementedInterfaces()) {
			   if(checkInheritanceRelationship(implementedInterface, finalSuperclass, visitedClasses)) {
				   return true;
			   }
		   }
	   }
	   UMLClass removedClass = getRemovedClass(subclass);
	   if(removedClass == null) {
		   removedClass = looksLikeRemovedClass(UMLType.extractTypeObject(subclass));
	   }
	   if(removedClass != null) {
		   UMLType superclass = removedClass.getSuperclass();
		   if(superclass != null) {
			   return checkInheritanceRelationship(superclass, finalSuperclass, visitedClasses);
		   }
		   for(UMLType implementedInterface : removedClass.getImplementedInterfaces()) {
			   if(checkInheritanceRelationship(implementedInterface, finalSuperclass, visitedClasses)) {
				   return true;
			   }
		   }
	   }
	   return false;
   }

   private boolean checkInheritanceRelationship(UMLType superclass, String finalSuperclass, Set<String> visitedClasses) {
	   if(looksLikeSameType(superclass.getClassType(), finalSuperclass))
		   return true;
	   else
		   return isSubclassOf(superclass.getClassType(), finalSuperclass, visitedClasses);
   }

   private UMLClass looksLikeAddedClass(UMLType type) {
	   for(UMLClass umlClass : addedClasses) {
	         if(umlClass.getName().endsWith("." + type.getClassType())) {
	        	 return umlClass;
	         }
	   }
	   return null;
   }

   private UMLClass looksLikeRemovedClass(UMLType type) {
	   for(UMLClass umlClass : removedClasses) {
	         if(umlClass.getName().endsWith("." + type.getClassType())) {
	        	 return umlClass;
	         }
	   }
	   return null;
   }

   public UMLClass getAddedClass(String className) {
      for(UMLClass umlClass : addedClasses) {
         if(umlClass.getName().equals(className))
            return umlClass;
      }
      return null;
   }

   public UMLClass getRemovedClass(String className) {
      for(UMLClass umlClass : removedClasses) {
         if(umlClass.getName().equals(className))
            return umlClass;
      }
      return null;
   }

   private String isRenamedClass(UMLClass umlClass) {
      for(UMLClassRenameDiff renameDiff : classRenameDiffList) {
         if(renameDiff.getOriginalClass().equals(umlClass))
            return renameDiff.getRenamedClass().getName();
      }
      return null;
   }

   private String isMovedClass(UMLClass umlClass) {
      for(UMLClassMoveDiff moveDiff : classMoveDiffList) {
         if(moveDiff.getOriginalClass().equals(umlClass))
            return moveDiff.getMovedClass().getName();
      }
      return null;
   }

   public void checkForGeneralizationChanges() {
      for(Iterator<UMLGeneralization> removedGeneralizationIterator = removedGeneralizations.iterator(); removedGeneralizationIterator.hasNext();) {
         UMLGeneralization removedGeneralization = removedGeneralizationIterator.next();
         for(Iterator<UMLGeneralization> addedGeneralizationIterator = addedGeneralizations.iterator(); addedGeneralizationIterator.hasNext();) {
            UMLGeneralization addedGeneralization = addedGeneralizationIterator.next();
            String renamedChild = isRenamedClass(removedGeneralization.getChild());
            String movedChild = isMovedClass(removedGeneralization.getChild());
            if(removedGeneralization.getChild().equals(addedGeneralization.getChild())) {
               UMLGeneralizationDiff generalizationDiff = new UMLGeneralizationDiff(removedGeneralization, addedGeneralization);
               addedGeneralizationIterator.remove();
               removedGeneralizationIterator.remove();
               generalizationDiffList.add(generalizationDiff);
               break;
            }
            if( (renamedChild != null && renamedChild.equals(addedGeneralization.getChild().getName())) ||
                  (movedChild != null && movedChild.equals(addedGeneralization.getChild().getName()))) {
               UMLGeneralizationDiff generalizationDiff = new UMLGeneralizationDiff(removedGeneralization, addedGeneralization);
               addedGeneralizationIterator.remove();
               removedGeneralizationIterator.remove();
               generalizationDiffList.add(generalizationDiff);
               break;
            }
         }
      }
   }

   public void checkForRealizationChanges() {
      for(Iterator<UMLRealization> removedRealizationIterator = removedRealizations.iterator(); removedRealizationIterator.hasNext();) {
         UMLRealization removedRealization = removedRealizationIterator.next();
         for(Iterator<UMLRealization> addedRealizationIterator = addedRealizations.iterator(); addedRealizationIterator.hasNext();) {
            UMLRealization addedRealization = addedRealizationIterator.next();
            String renamedChild = isRenamedClass(removedRealization.getClient());
            String movedChild = isMovedClass(removedRealization.getClient());
            //String renamedParent = isRenamedClass(removedRealization.getSupplier());
            //String movedParent = isMovedClass(removedRealization.getSupplier());
            if( (renamedChild != null && renamedChild.equals(addedRealization.getClient().getName())) ||
                  (movedChild != null && movedChild.equals(addedRealization.getClient().getName()))) {
               UMLRealizationDiff realizationDiff = new UMLRealizationDiff(removedRealization, addedRealization);
               addedRealizationIterator.remove();
               removedRealizationIterator.remove();
               realizationDiffList.add(realizationDiff);
               break;
            }
         }
      }
   }

   public void checkForMovedClasses(Map<String, String> renamedFileHints, Set<String> repositoryDirectories, UMLClassMatcher matcher) {
	   for(Iterator<UMLClass> removedClassIterator = removedClasses.iterator(); removedClassIterator.hasNext();) {
		   UMLClass removedClass = removedClassIterator.next();
		   TreeSet<UMLClassMoveDiff> diffSet = new TreeSet<UMLClassMoveDiff>(new ClassMoveComparator());
		   for(Iterator<UMLClass> addedClassIterator = addedClasses.iterator(); addedClassIterator.hasNext();) {
			   UMLClass addedClass = addedClassIterator.next();
			   String removedClassSourceFile = removedClass.getSourceFile();
			   String renamedFile =  renamedFileHints.get(removedClassSourceFile);
			   String removedClassSourceFolder = "";
			   if(removedClassSourceFile.contains("/")) {
				   removedClassSourceFolder = removedClassSourceFile.substring(0, removedClassSourceFile.lastIndexOf("/"));
			   }
			   if(!repositoryDirectories.contains(removedClassSourceFolder)) {
				   deletedFolderPaths.add(removedClassSourceFolder);
			   }
			   if(matcher.match(removedClass, addedClass, renamedFile)) {
				   if(!conflictingMoveOfTopLevelClass(removedClass, addedClass)) {
					   UMLClassMoveDiff classMoveDiff = new UMLClassMoveDiff(removedClass, addedClass, this);
					   diffSet.add(classMoveDiff);
				   }
			   }
		   }
		   if(!diffSet.isEmpty()) {
			   UMLClassMoveDiff minClassMoveDiff = diffSet.first();
			   minClassMoveDiff.process();
			   classMoveDiffList.add(minClassMoveDiff);
			   addedClasses.remove(minClassMoveDiff.getMovedClass());
			   removedClassIterator.remove();
		   }
	   }

	   List<UMLClassMoveDiff> allClassMoves = new ArrayList<UMLClassMoveDiff>(this.classMoveDiffList);
	   Collections.sort(allClassMoves);

	   for(int i=0; i<allClassMoves.size(); i++) {
		   UMLClassMoveDiff classMoveI = allClassMoves.get(i);
		   for(int j=i+1; j<allClassMoves.size(); j++) {
			   UMLClassMoveDiff classMoveJ = allClassMoves.get(j);
			   if(classMoveI.isInnerClassMove(classMoveJ)) {
				   innerClassMoveDiffList.add(classMoveJ);
			   }
		   }
	   }
	   this.classMoveDiffList.removeAll(innerClassMoveDiffList);
   }

   private boolean conflictingMoveOfTopLevelClass(UMLClass removedClass, UMLClass addedClass) {
	   if(!removedClass.isTopLevel() && !addedClass.isTopLevel()) {
		   //check if classMoveDiffList contains already a move for the outer class to a different target
		   for(UMLClassMoveDiff diff : classMoveDiffList) {
			   if((diff.getOriginalClass().getName().startsWith(removedClass.getPackageName()) &&
					   !diff.getMovedClass().getName().startsWith(addedClass.getPackageName())) ||
					   (!diff.getOriginalClass().getName().startsWith(removedClass.getPackageName()) &&
						diff.getMovedClass().getName().startsWith(addedClass.getPackageName()))) {
				   return true;
			   }
		   }
	   }
	   return false;
   }

   public void checkForRenamedClasses(Map<String, String> renamedFileHints, UMLClassMatcher matcher) {
      for(Iterator<UMLClass> removedClassIterator = removedClasses.iterator(); removedClassIterator.hasNext();) {
         UMLClass removedClass = removedClassIterator.next();
         TreeSet<UMLClassRenameDiff> diffSet = new TreeSet<UMLClassRenameDiff>(new ClassRenameComparator());
         for(Iterator<UMLClass> addedClassIterator = addedClasses.iterator(); addedClassIterator.hasNext();) {
            UMLClass addedClass = addedClassIterator.next();
            String renamedFile =  renamedFileHints.get(removedClass.getSourceFile());
            if(matcher.match(removedClass, addedClass, renamedFile)) {
               if(!conflictingMoveOfTopLevelClass(removedClass, addedClass) && !innerClassWithTheSameName(removedClass, addedClass)) {
            	   UMLClassRenameDiff classRenameDiff = new UMLClassRenameDiff(removedClass, addedClass, this);
            	   diffSet.add(classRenameDiff);
               }
            }
         }
         if(!diffSet.isEmpty()) {
            UMLClassRenameDiff minClassRenameDiff = diffSet.first();
            minClassRenameDiff.process();
            classRenameDiffList.add(minClassRenameDiff);
            addedClasses.remove(minClassRenameDiff.getRenamedClass());
            removedClassIterator.remove();
         }
      }
      
      List<UMLClassMoveDiff> allClassMoves = new ArrayList<UMLClassMoveDiff>(this.classMoveDiffList);
      Collections.sort(allClassMoves);
      
      for(UMLClassRenameDiff classRename : classRenameDiffList) {
         for(UMLClassMoveDiff classMove : allClassMoves) {
            if(classRename.isInnerClassMove(classMove)) {
               innerClassMoveDiffList.add(classMove);
            }
         }
      }
      this.classMoveDiffList.removeAll(innerClassMoveDiffList);
   }

   private boolean innerClassWithTheSameName(UMLClass removedClass, UMLClass addedClass) {
	   if(!removedClass.isTopLevel() && !addedClass.isTopLevel()) {
		   String removedClassName = removedClass.getName();
		   String removedName = removedClassName.substring(removedClassName.lastIndexOf(".")+1, removedClassName.length());
		   String addedClassName = addedClass.getName();
		   String addedName = addedClassName.substring(addedClassName.lastIndexOf(".")+1, addedClassName.length());
		   if(removedName.equals(addedName)) {
			   return true;
		   }
	   }
	   return false;
   }

   public List<UMLGeneralization> getAddedGeneralizations() {
      return addedGeneralizations;
   }

   public List<UMLRealization> getAddedRealizations() {
      return addedRealizations;
   }

   private List<MoveAttributeRefactoring> checkForAttributeMovesIncludingRemovedClasses() {
      List<UMLAttribute> addedAttributes = getAddedAttributesInCommonClasses();
      /*for(UMLClass addedClass : addedClasses) {
    	  addedAttributes.addAll(addedClass.getAttributes());
      }*/
      List<UMLAttribute> removedAttributes = getRemovedAttributesInCommonClasses();
      for(UMLClass removedClass : removedClasses) {
    	  removedAttributes.addAll(removedClass.getAttributes());
      }
      return checkForAttributeMoves(addedAttributes, removedAttributes);
   }

   private List<MoveAttributeRefactoring> checkForAttributeMovesIncludingAddedClasses() {
      List<UMLAttribute> addedAttributes = getAddedAttributesInCommonClasses();
      for(UMLClass addedClass : addedClasses) {
    	  addedAttributes.addAll(addedClass.getAttributes());
      }
      List<UMLAttribute> removedAttributes = getRemovedAttributesInCommonClasses();
      /*for(UMLClass removedClass : removedClasses) {
    	  removedAttributes.addAll(removedClass.getAttributes());
      }*/
      return checkForAttributeMoves(addedAttributes, removedAttributes);
   }

   private List<MoveAttributeRefactoring> checkForAttributeMovesBetweenCommonClasses() {
      List<UMLAttribute> addedAttributes = getAddedAttributesInCommonClasses();
      List<UMLAttribute> removedAttributes = getRemovedAttributesInCommonClasses();
      return checkForAttributeMoves(addedAttributes, removedAttributes);
   }

   private List<MoveAttributeRefactoring> checkForAttributeMovesBetweenRemovedAndAddedClasses() {
	   List<UMLAttribute> addedAttributes = new ArrayList<UMLAttribute>();
	   for(UMLClass addedClass : addedClasses) {
		   addedAttributes.addAll(addedClass.getAttributes());
	   }
	   List<UMLAttribute> removedAttributes = new ArrayList<UMLAttribute>();
	   for(UMLClass removedClass : removedClasses) {
		   removedAttributes.addAll(removedClass.getAttributes());
	   }
	   return checkForAttributeMoves(addedAttributes, removedAttributes);
   }

   private List<MoveAttributeRefactoring> checkForAttributeMoves(List<UMLAttribute> addedAttributes, List<UMLAttribute> removedAttributes) {
	   List<MoveAttributeRefactoring> refactorings = new ArrayList<MoveAttributeRefactoring>();
	   if(addedAttributes.size() <= removedAttributes.size()) {
		   for(UMLAttribute addedAttribute : addedAttributes) {
			   List<MoveAttributeRefactoring> candidates = new ArrayList<MoveAttributeRefactoring>();
			   for(UMLAttribute removedAttribute : removedAttributes) {
				   MoveAttributeRefactoring candidate = processPairOfAttributes(addedAttribute, removedAttribute);
				   if(candidate != null) {
					   candidates.add(candidate);
				   }
			   }
			   processCandidates(candidates, refactorings);
		   }
	   }
	   else {
		   for(UMLAttribute removedAttribute : removedAttributes) {
			   List<MoveAttributeRefactoring> candidates = new ArrayList<MoveAttributeRefactoring>();
			   for(UMLAttribute addedAttribute : addedAttributes) {
				   MoveAttributeRefactoring candidate = processPairOfAttributes(addedAttribute, removedAttribute);
				   if(candidate != null) {
					   candidates.add(candidate);
				   }
			   }
			   processCandidates(candidates, refactorings);
		   }
	   }
      return refactorings;
   }

   private void processCandidates(List<MoveAttributeRefactoring> candidates, List<MoveAttributeRefactoring> refactorings) {
	   if(candidates.size() > 1) {
		   TreeMap<Integer, List<MoveAttributeRefactoring>> map = new TreeMap<Integer, List<MoveAttributeRefactoring>>();
		   for(MoveAttributeRefactoring candidate : candidates) {
			   int compatibility = computeCompatibility(candidate);
			   if(map.containsKey(compatibility)) {
				   map.get(compatibility).add(candidate);
			   }
			   else {
				   List<MoveAttributeRefactoring> refs = new ArrayList<MoveAttributeRefactoring>();
				   refs.add(candidate);
				   map.put(compatibility, refs);
			   }
		   }
		   int maxCompatibility = map.lastKey();
		   refactorings.addAll(map.get(maxCompatibility));
	   }
	   else if(candidates.size() == 1) {
		   refactorings.addAll(candidates);
	   }
   }

   private MoveAttributeRefactoring processPairOfAttributes(UMLAttribute addedAttribute, UMLAttribute removedAttribute) {
	   if(addedAttribute.getName().equals(removedAttribute.getName()) &&
			   addedAttribute.getType().equals(removedAttribute.getType())) {
		   if(isSubclassOf(removedAttribute.getClassName(), addedAttribute.getClassName())) {
			   PullUpAttributeRefactoring pullUpAttribute = new PullUpAttributeRefactoring(removedAttribute, addedAttribute);
			   return pullUpAttribute;
		   }
		   else if(isSubclassOf(addedAttribute.getClassName(), removedAttribute.getClassName())) {
			   PushDownAttributeRefactoring pushDownAttribute = new PushDownAttributeRefactoring(removedAttribute, addedAttribute);
			   return pushDownAttribute;
		   }
		   else if(sourceClassImportsTargetClassAfterRefactoring(removedAttribute.getClassName(), addedAttribute.getClassName()) ||
				   targetClassImportsSourceClassBeforeRefactoring(removedAttribute.getClassName(), addedAttribute.getClassName())) {
			   MoveAttributeRefactoring moveAttribute = new MoveAttributeRefactoring(removedAttribute, addedAttribute);
			   return moveAttribute;
		   }
	   }
	   return null;
   }

   private int computeCompatibility(MoveAttributeRefactoring candidate) {
	   int count = 0;
	   for(Refactoring ref : refactorings) {
		   if(ref instanceof MoveOperationRefactoring) {
			   MoveOperationRefactoring moveRef = (MoveOperationRefactoring)ref;
			   if(moveRef.compatibleWith(candidate)) {
				   count++;
			   }
		   }
	   }
	   UMLClassBaseDiff sourceClassDiff = getUMLClassDiff(candidate.getSourceClassName());
	   UMLClassBaseDiff targetClassDiff = getUMLClassDiff(candidate.getTargetClassName());
	   if(sourceClassDiff != null) {
		   UMLType targetSuperclass = null;
		   if(targetClassDiff != null) {
			   targetSuperclass = targetClassDiff.getSuperclass();
		   }
		   List<UMLAttribute> addedAttributes = sourceClassDiff.getAddedAttributes();
		   for(UMLAttribute addedAttribute : addedAttributes) {
			   if(looksLikeSameType(addedAttribute.getType().getClassType(), candidate.getTargetClassName())) {
				   count++;
			   }
			   if(targetSuperclass != null && looksLikeSameType(addedAttribute.getType().getClassType(), targetSuperclass.getClassType())) {
				   count++;
			   }
		   }
		   List<UMLAttribute> originalAttributes = sourceClassDiff.originalClassAttributesOfType(candidate.getTargetClassName());
		   List<UMLAttribute> nextAttributes = sourceClassDiff.nextClassAttributesOfType(candidate.getTargetClassName());
		   if(targetSuperclass != null) {
			   originalAttributes.addAll(sourceClassDiff.originalClassAttributesOfType(targetSuperclass.getClassType()));
			   nextAttributes.addAll(sourceClassDiff.nextClassAttributesOfType(targetSuperclass.getClassType()));
		   }
		   Set<UMLAttribute> intersection = new LinkedHashSet<UMLAttribute>(originalAttributes);
		   intersection.retainAll(nextAttributes);
		   if(!intersection.isEmpty()) {
			   count++;
		   }
	   }
	   return count;
   }

   private boolean sourceClassImportsSuperclassOfTargetClassAfterRefactoring(String sourceClassName, String targetClassName) {
	   UMLClassBaseDiff targetClassDiff = getUMLClassDiff(targetClassName);
	   if(targetClassDiff != null && targetClassDiff.getSuperclass() != null) {
		   UMLClassBaseDiff superclassOfTargetClassDiff = getUMLClassDiff(targetClassDiff.getSuperclass());
		   if(superclassOfTargetClassDiff != null) {
			   return sourceClassImportsTargetClassAfterRefactoring(sourceClassName, superclassOfTargetClassDiff.getNextClassName());
		   }
	   }
	   return false;
   }

   private boolean sourceClassImportsTargetClassAfterRefactoring(String sourceClassName, String targetClassName) {
	   UMLClassBaseDiff classDiff = getUMLClassDiff(sourceClassName);
	   if(classDiff == null) {
		   classDiff = getUMLClassDiff(UMLType.extractTypeObject(sourceClassName));
	   }
	   if(classDiff != null) {
		   return classDiff.nextClassImportsType(targetClassName);
	   }
	   UMLClass removedClass = getRemovedClass(sourceClassName);
	   if(removedClass == null) {
		   removedClass = looksLikeRemovedClass(UMLType.extractTypeObject(sourceClassName));
	   }
	   if(removedClass != null) {
		   return removedClass.importsType(targetClassName);
	   }
	   return false;
   }

   private boolean targetClassImportsSourceClassBeforeRefactoring(String sourceClassName, String targetClassName) {
	   UMLClassBaseDiff classDiff = getUMLClassDiff(targetClassName);
	   if(classDiff == null) {
		   classDiff = getUMLClassDiff(UMLType.extractTypeObject(targetClassName));
	   }
	   if(classDiff != null) {
		   return classDiff.originalClassImportsType(sourceClassName);
	   }
	   UMLClass addedClass = getAddedClass(targetClassName);
	   if(addedClass == null) {
		   addedClass = looksLikeAddedClass(UMLType.extractTypeObject(targetClassName));
	   }
	   if(addedClass != null) {
		   return addedClass.importsType(sourceClassName);
	   }
	   return false;
   }

   private List<UMLAttribute> getAddedAttributesInCommonClasses() {
      List<UMLAttribute> addedAttributes = new ArrayList<UMLAttribute>();
      for(UMLClassDiff classDiff : commonClassDiffList) {
         addedAttributes.addAll(classDiff.getAddedAttributes());
      }
      return addedAttributes;
   }

   private List<UMLAttribute> getRemovedAttributesInCommonClasses() {
      List<UMLAttribute> removedAttributes = new ArrayList<UMLAttribute>();
      for(UMLClassDiff classDiff : commonClassDiffList) {
         removedAttributes.addAll(classDiff.getRemovedAttributes());
      }
      return removedAttributes;
   }

   private List<UMLOperation> getAddedOperationsInCommonClasses() {
      List<UMLOperation> addedOperations = new ArrayList<UMLOperation>();
      for(UMLClassDiff classDiff : commonClassDiffList) {
         addedOperations.addAll(classDiff.getAddedOperations());
      }
      return addedOperations;
   }

   private List<UMLOperation> getAddedAndExtractedOperationsInCommonClasses() {
      List<UMLOperation> addedOperations = new ArrayList<UMLOperation>();
      for(UMLClassDiff classDiff : commonClassDiffList) {
         addedOperations.addAll(classDiff.getAddedOperations());
         for(Refactoring ref : classDiff.getRefactorings()) {
        	 if(ref instanceof ExtractOperationRefactoring) {
        		 ExtractOperationRefactoring extractRef = (ExtractOperationRefactoring)ref;
        		 addedOperations.add(extractRef.getExtractedOperation());
        	 }
         }
      }
      return addedOperations;
   }

   private List<UMLOperation> getAddedOperationsInMovedAndRenamedClasses() {
      List<UMLOperation> addedOperations = new ArrayList<UMLOperation>();
      for(UMLClassMoveDiff classDiff : innerClassMoveDiffList) {
         addedOperations.addAll(classDiff.getAddedOperations());
      }
      for(UMLClassMoveDiff classDiff : classMoveDiffList) {
         addedOperations.addAll(classDiff.getAddedOperations());
      }
      for(UMLClassRenameDiff classDiff : classRenameDiffList) {
         addedOperations.addAll(classDiff.getAddedOperations());
      }
      return addedOperations;
   }

   private List<UMLOperation> getRemovedOperationsInCommonClasses() {
      List<UMLOperation> removedOperations = new ArrayList<UMLOperation>();
      for(UMLClassDiff classDiff : commonClassDiffList) {
         removedOperations.addAll(classDiff.getRemovedOperations());
      }
      return removedOperations;
   }
   
   private List<UMLOperationBodyMapper> getOperationBodyMappersInCommonClasses() {
      List<UMLOperationBodyMapper> mappers = new ArrayList<UMLOperationBodyMapper>();
      for(UMLClassDiff classDiff : commonClassDiffList) {
         mappers.addAll(classDiff.getOperationBodyMapperList());
      }
      return mappers;
   }

   private List<UMLOperationBodyMapper> getOperationBodyMappersInMovedAndRenamedClasses() {
	   List<UMLOperationBodyMapper> mappers = new ArrayList<UMLOperationBodyMapper>();
	   for(UMLClassMoveDiff classDiff : classMoveDiffList) {
		   mappers.addAll(classDiff.getOperationBodyMapperList());
	   }
	   for(UMLClassMoveDiff classDiff : innerClassMoveDiffList) {
		   mappers.addAll(classDiff.getOperationBodyMapperList());
	   }
	   for(UMLClassRenameDiff classDiff : classRenameDiffList) {
		   mappers.addAll(classDiff.getOperationBodyMapperList());
	   }
	   return mappers;
   }

   private List<ExtractClassRefactoring> identifyExtractClassRefactorings(List<? extends UMLClassBaseDiff> classDiffs) {
	   List<ExtractClassRefactoring> refactorings = new ArrayList<ExtractClassRefactoring>();
	   for(UMLClass addedClass : addedClasses) {
		   List<CandidateExtractClassRefactoring> candidates = new ArrayList<CandidateExtractClassRefactoring>();
		   UMLType addedClassSuperType = addedClass.getSuperclass();
		   if(!addedClass.isInterface()) {
			   for(UMLClassBaseDiff classDiff : classDiffs) {
				   UMLType classDiffSuperType = classDiff.getNewSuperclass();
				   boolean commonSuperType = addedClassSuperType != null && classDiffSuperType != null &&
						   addedClassSuperType.getClassType().equals(classDiffSuperType.getClassType());
				   boolean commonInterface = false;
				   for(UMLType addedClassInterface : addedClass.getImplementedInterfaces()) {
					   for(UMLType classDiffInterface : classDiff.getNextClass().getImplementedInterfaces()) {
						   if(addedClassInterface.getClassType().equals(classDiffInterface.getClassType())) {
							   commonInterface = true;
							   break;
						   }
					   }
					   if(commonInterface)
						   break;
				   }
				   boolean extendsAddedClass = classDiff.getNewSuperclass() != null &&
						   addedClass.getName().endsWith("." + classDiff.getNewSuperclass().getClassType());
				   UMLAttribute attributeOfExtractedClassType = attributeOfExtractedClassType(addedClass, classDiff);
				   boolean isTestClass =  addedClass.isTestClass() && classDiff.getOriginalClass().isTestClass();
				   if((!commonSuperType && !commonInterface && !extendsAddedClass) || attributeOfExtractedClassType != null || isTestClass) {
					   ExtractClassRefactoring refactoring = atLeastOneCommonAttributeOrOperation(addedClass, classDiff, attributeOfExtractedClassType);
					   if(refactoring != null) {
						   CandidateExtractClassRefactoring candidate = new CandidateExtractClassRefactoring(classDiff, refactoring);
						   candidates.add(candidate);
					   }
				   }
			   }
		   }
		   if(!candidates.isEmpty()) {
			   boolean innerClassExtract = false;
			   for(CandidateExtractClassRefactoring candidate : candidates) {
				   if(candidate.innerClassExtract()) {
					   innerClassExtract = true;
					   detectSubRefactorings(candidate.getClassDiff(),
							   candidate.getRefactoring().getExtractedClass(),
							   candidate.getRefactoring().getRefactoringType());
					   refactorings.add(candidate.getRefactoring());
					   break;
				   }
			   }
			   if(!innerClassExtract) {
				   for(CandidateExtractClassRefactoring candidate : candidates) {
					   detectSubRefactorings(candidate.getClassDiff(),
							   candidate.getRefactoring().getExtractedClass(),
							   candidate.getRefactoring().getRefactoringType());
					   refactorings.add(candidate.getRefactoring());
				   }
			   }
		   }
	   }
	   return refactorings;
   }

   private UMLAttribute attributeOfExtractedClassType(UMLClass umlClass, UMLClassBaseDiff classDiff) {
	   List<UMLAttribute> addedAttributes = classDiff.getAddedAttributes();
	   for(UMLAttribute addedAttribute : addedAttributes) {
		   if(umlClass.getName().endsWith("." + addedAttribute.getType().getClassType())) {
			   return addedAttribute;
		   }
	   }
	   return null;
   }

   private ExtractClassRefactoring atLeastOneCommonAttributeOrOperation(UMLClass umlClass, UMLClassBaseDiff classDiff, UMLAttribute attributeOfExtractedClassType) {
	   Set<UMLOperation> commonOperations = new LinkedHashSet<UMLOperation>();
	   for(UMLOperation operation : classDiff.getRemovedOperations()) {
		   if(!operation.isConstructor() && !operation.overridesObject()) {
			   if(umlClass.containsOperationWithTheSameSignatureIgnoringChangedTypes(operation)) {
				   commonOperations.add(operation);
			   }
		   }
	   }
	   Set<UMLAttribute> commonAttributes = new LinkedHashSet<UMLAttribute>();
	   for(UMLAttribute attribute : classDiff.getRemovedAttributes()) {
		   if(umlClass.containsAttributeWithTheSameNameIgnoringChangedType(attribute)) {
			   commonAttributes.add(attribute);
		   }
	   }
	   int threshold = 1;
	   if(attributeOfExtractedClassType != null)
		   threshold = 0;
	   if(commonOperations.size() > threshold || commonAttributes.size() > threshold) {
		   return new ExtractClassRefactoring(umlClass, classDiff.getNextClass(), commonOperations, commonAttributes, attributeOfExtractedClassType);
	   }
	   return null;
   }

   private List<ExtractSuperclassRefactoring> identifyExtractSuperclassRefactorings() {
      List<ExtractSuperclassRefactoring> refactorings = new ArrayList<ExtractSuperclassRefactoring>();
      for(UMLClass addedClass : addedClasses) {
         Set<UMLClass> subclassSet = new LinkedHashSet<UMLClass>();
         String addedClassName = addedClass.getName();
         for(UMLGeneralization addedGeneralization : addedGeneralizations) {
        	 processAddedGeneralization(addedClass, subclassSet, addedGeneralization);
         }
         for(UMLGeneralizationDiff generalizationDiff : generalizationDiffList) {
        	 UMLGeneralization addedGeneralization = generalizationDiff.getAddedGeneralization();
        	 UMLGeneralization removedGeneralization = generalizationDiff.getRemovedGeneralization();
        	 if(!addedGeneralization.getParent().equals(removedGeneralization.getParent())) {
        		 processAddedGeneralization(addedClass, subclassSet, addedGeneralization);
        	 }
         }
         for(UMLRealization addedRealization : addedRealizations) {
            String supplier = addedRealization.getSupplier();
			if(looksLikeSameType(supplier, addedClassName) && topLevelOrSameOuterClass(addedClass, addedRealization.getClient()) && getAddedClass(addedRealization.getClient().getName()) == null) {
               UMLClassBaseDiff clientClassDiff = getUMLClassDiff(addedRealization.getClient().getName());
               boolean implementedInterfaceOperations = true;
               if(clientClassDiff != null) {
                  for(UMLOperation interfaceOperation : addedClass.getOperations()) {
                     if(!clientClassDiff.containsOperationWithTheSameSignature(interfaceOperation)) {
                        implementedInterfaceOperations = false;
                        break;
                     }
                  }
               }
               if(implementedInterfaceOperations)
                  subclassSet.add(addedRealization.getClient());
            }
         }
         if(subclassSet.size() > 0) {
            ExtractSuperclassRefactoring extractSuperclassRefactoring = new ExtractSuperclassRefactoring(addedClass, subclassSet);
            refactorings.add(extractSuperclassRefactoring);
         }
      }
      return refactorings;
   }

   private void processAddedGeneralization(UMLClass addedClass, Set<UMLClass> subclassSet, UMLGeneralization addedGeneralization) {
	   String parent = addedGeneralization.getParent();
	   UMLClass subclass = addedGeneralization.getChild();
	   if(looksLikeSameType(parent, addedClass.getName()) && topLevelOrSameOuterClass(addedClass, subclass) && getAddedClass(subclass.getName()) == null) {
		   UMLClassBaseDiff subclassDiff = getUMLClassDiff(subclass.getName());
		   if(subclassDiff != null) {
			   detectSubRefactorings(subclassDiff, addedClass, RefactoringType.EXTRACT_SUPERCLASS);
		   }
		   subclassSet.add(subclass);
	   }
   }

   private void detectSubRefactorings(UMLClassBaseDiff classDiff, UMLClass addedClass, RefactoringType parentType) {
	   for(UMLOperation addedOperation : addedClass.getOperations()) {
		   UMLOperation removedOperation = classDiff.containsRemovedOperationWithTheSameSignature(addedOperation);
		   if(removedOperation != null) {
			   classDiff.getRemovedOperations().remove(removedOperation);
			   Refactoring ref = null;
			   if(parentType.equals(RefactoringType.EXTRACT_SUPERCLASS)) {
				   ref = new PullUpOperationRefactoring(removedOperation, addedOperation);
			   }
			   else if(parentType.equals(RefactoringType.EXTRACT_CLASS)) {
				   ref = new MoveOperationRefactoring(removedOperation, addedOperation);
			   }
			   else if(parentType.equals(RefactoringType.EXTRACT_SUBCLASS)) {
				   ref = new PushDownOperationRefactoring(removedOperation, addedOperation);
			   }
			   this.refactorings.add(ref);
			   UMLOperationBodyMapper mapper = new UMLOperationBodyMapper(removedOperation, addedOperation);
			   checkForExtractedOperationsWithinMovedMethod(mapper, addedClass);
		   }
	   }
	   for(UMLAttribute addedAttribute : addedClass.getAttributes()) {
		   UMLAttribute removedAttribute = classDiff.containsRemovedAttributeWithTheSameSignature(addedAttribute);
		   if(removedAttribute != null) {
			   classDiff.getRemovedAttributes().remove(removedAttribute);
			   Refactoring ref = null;
			   if(parentType.equals(RefactoringType.EXTRACT_SUPERCLASS)) {
				   ref = new PullUpAttributeRefactoring(removedAttribute, addedAttribute);
			   }
			   else if(parentType.equals(RefactoringType.EXTRACT_CLASS)) {
				   ref = new MoveAttributeRefactoring(removedAttribute, addedAttribute);
			   }
			   else if(parentType.equals(RefactoringType.EXTRACT_SUBCLASS)) {
				   ref = new PushDownAttributeRefactoring(removedAttribute, addedAttribute);
			   }
			   this.refactorings.add(ref);
		   }
	   }
   }

   private void checkForExtractedOperationsWithinMovedMethod(UMLOperationBodyMapper movedMethodMapper, UMLClass addedClass) {
	   UMLOperation removedOperation = movedMethodMapper.getOperation1();
	   UMLOperation addedOperation = movedMethodMapper.getOperation2();
	   Set<OperationInvocation> removedInvocations = removedOperation.getAllOperationInvocations();
	   Set<OperationInvocation> addedInvocations = addedOperation.getAllOperationInvocations();
	   Set<OperationInvocation> intersection = new LinkedHashSet<OperationInvocation>(removedInvocations);
	   intersection.retainAll(addedInvocations);
	   Set<OperationInvocation> newInvocations = new LinkedHashSet<OperationInvocation>(addedInvocations);
	   newInvocations.removeAll(intersection);
	   for(OperationInvocation newInvocation : newInvocations) {
		   for(UMLOperation operation : addedClass.getOperations()) {
			   if(!operation.isAbstract() && !operation.hasEmptyBody() &&
					   newInvocation.matchesOperation(operation, addedOperation.variableTypeMap(), this)) {
				   ExtractOperationDetection detection = new ExtractOperationDetection(addedClass.getOperations(), this);
				   ExtractOperationRefactoring refactoring = detection.check(movedMethodMapper, operation);
				   if(refactoring != null) {
					  this.refactorings.add(refactoring);
				   }
			   }
		   }
	   }
   }

   private boolean topLevelOrSameOuterClass(UMLClass class1, UMLClass class2) {
	   if(!class1.isTopLevel() && !class2.isTopLevel()) {
		   return class1.getPackageName().equals(class2.getPackageName());
	   }
	   return true;
   }

   public static boolean looksLikeSameType(String parent, String addedClassName) {
      if (addedClassName.contains(".") && !parent.contains(".")) {
         return parent.equals(addedClassName.substring(addedClassName.lastIndexOf(".") + 1));
      }
      if (parent.contains(".") && !addedClassName.contains(".")) {
         return addedClassName.equals(parent.substring(parent.lastIndexOf(".") + 1));
      }
      if (parent.contains(".") && addedClassName.contains(".")) {
    	  return UMLType.extractTypeObject(parent).equalClassType(UMLType.extractTypeObject(addedClassName));
      }
      return parent.equals(addedClassName);
   }

   private List<ConvertAnonymousClassToTypeRefactoring> identifyConvertAnonymousClassToTypeRefactorings() {
      List<ConvertAnonymousClassToTypeRefactoring> refactorings = new ArrayList<ConvertAnonymousClassToTypeRefactoring>();
      for(UMLClassDiff classDiff : commonClassDiffList) {
	      for(UMLAnonymousClass anonymousClass : classDiff.getRemovedAnonymousClasses()) {
	         for(UMLClass addedClass : addedClasses) {
	            if(addedClass.getAttributes().containsAll(anonymousClass.getAttributes()) &&
	                  addedClass.getOperations().containsAll(anonymousClass.getOperations())) {
	               ConvertAnonymousClassToTypeRefactoring refactoring = new ConvertAnonymousClassToTypeRefactoring(anonymousClass, addedClass);
	               refactorings.add(refactoring);
	            }
	         }
	      }
      }
      return refactorings;
   }

   private List<Refactoring> getMoveClassRefactorings() {
	   List<Refactoring> refactorings = new ArrayList<Refactoring>();
	   List<RenamePackageRefactoring> renamePackageRefactorings = new ArrayList<RenamePackageRefactoring>();
	   List<MoveSourceFolderRefactoring> moveSourceFolderRefactorings = new ArrayList<MoveSourceFolderRefactoring>();
	   for(UMLClassMoveDiff classMoveDiff : classMoveDiffList) {
		   UMLClass originalClass = classMoveDiff.getOriginalClass();
		   String originalName = originalClass.getName();
		   UMLClass movedClass = classMoveDiff.getMovedClass();
		   String movedName = movedClass.getName();
		   
		   String originalPath = originalClass.getSourceFile();
		   String movedPath = movedClass.getSourceFile();
		   String originalPathPrefix = "";
		   if(originalPath.contains("/")) {
			   originalPathPrefix = originalPath.substring(0, originalPath.lastIndexOf('/'));
		   }
		   String movedPathPrefix = "";
		   if(movedPath.contains("/")) {
			   movedPathPrefix = movedPath.substring(0, movedPath.lastIndexOf('/'));
		   }
		   
		   if (!originalName.equals(movedName)) {
			   MoveClassRefactoring refactoring = new MoveClassRefactoring(originalClass, movedClass);
			   RenamePattern renamePattern = refactoring.getRenamePattern();
			   //check if the the original path is a substring of the moved path and vice versa
			   if(renamePattern.getBefore().contains(renamePattern.getAfter()) ||
					   renamePattern.getAfter().contains(renamePattern.getBefore()) ||
					   !originalClass.isTopLevel() || !movedClass.isTopLevel()) {
				   refactorings.add(refactoring);
			   }
			   else {
				   boolean foundInMatchingRenamePackageRefactoring = false;
				   for(RenamePackageRefactoring renamePackageRefactoring : renamePackageRefactorings) {
					   if(renamePackageRefactoring.getPattern().equals(renamePattern)) {
						   renamePackageRefactoring.addMoveClassRefactoring(refactoring);
						   foundInMatchingRenamePackageRefactoring = true;
						   break;
					   }
				   }
				   if(!foundInMatchingRenamePackageRefactoring) {
					   renamePackageRefactorings.add(new RenamePackageRefactoring(refactoring));
				   }
			   }
		   } else if(!originalPathPrefix.equals(movedPathPrefix)) {
			   MovedClassToAnotherSourceFolder refactoring = new MovedClassToAnotherSourceFolder(originalName, originalPathPrefix, movedPathPrefix);
			   RenamePattern renamePattern = refactoring.getRenamePattern();
			   boolean foundInMatchingMoveSourceFolderRefactoring = false;
			   for(MoveSourceFolderRefactoring moveSourceFolderRefactoring : moveSourceFolderRefactorings) {
				   if(moveSourceFolderRefactoring.getPattern().equals(renamePattern)) {
					   moveSourceFolderRefactoring.addMovedClassToAnotherSourceFolder(refactoring);
					   foundInMatchingMoveSourceFolderRefactoring = true;
					   break;
				   }
			   }
			   if(!foundInMatchingMoveSourceFolderRefactoring) {
				   moveSourceFolderRefactorings.add(new MoveSourceFolderRefactoring(refactoring));
			   }
		   }
	   }
	   for(RenamePackageRefactoring renamePackageRefactoring : renamePackageRefactorings) {
		   List<MoveClassRefactoring> moveClassRefactorings = renamePackageRefactoring.getMoveClassRefactorings();
		   if(moveClassRefactorings.size() > 1 && isSourcePackageDeleted(renamePackageRefactoring)) {
			   refactorings.add(renamePackageRefactoring);
		   }
		   else {
			   refactorings.addAll(moveClassRefactorings);
		   }
	   }
	   refactorings.addAll(moveSourceFolderRefactorings);
	   return refactorings;
   }

   private boolean isSourcePackageDeleted(RenamePackageRefactoring renamePackageRefactoring) {
	   for(String deletedFolderPath : deletedFolderPaths) {
		   String originalPath = renamePackageRefactoring.getPattern().getBefore();
		   //remove last .
		   String trimmedOriginalPath = originalPath.endsWith(".") ? originalPath.substring(0, originalPath.length()-1) : originalPath;
		   String convertedPackageToFilePath = trimmedOriginalPath.replaceAll("\\.", "/");
		   if(deletedFolderPath.endsWith(convertedPackageToFilePath)) {
			   return true;
		   }
	   }
	   return false;
   }

   private List<Refactoring> getRenameClassRefactorings() {
      List<Refactoring> refactorings = new ArrayList<Refactoring>();
      for(UMLClassRenameDiff classRenameDiff : classRenameDiffList) {
    	  Refactoring refactoring = null;
    	  if(classRenameDiff.samePackage())
    		  refactoring = new RenameClassRefactoring(classRenameDiff.getOriginalClass(), classRenameDiff.getRenamedClass());
    	  else
    		  refactoring = new MoveAndRenameClassRefactoring(classRenameDiff.getOriginalClass(), classRenameDiff.getRenamedClass());
         refactorings.add(refactoring);
      }
      return refactorings;
   }

   public List<Refactoring> getRefactorings() {
      Set<Refactoring> refactorings = new LinkedHashSet<Refactoring>();
      refactorings.addAll(getMoveClassRefactorings());
      refactorings.addAll(getRenameClassRefactorings());
      refactorings.addAll(identifyConvertAnonymousClassToTypeRefactorings());
      refactorings.addAll(identifyExtractSuperclassRefactorings());
      Map<Replacement, Set<CandidateAttributeRefactoring>> map = new LinkedHashMap<Replacement, Set<CandidateAttributeRefactoring>>();
      for(UMLClassDiff classDiff : commonClassDiffList) {
         refactorings.addAll(classDiff.getRefactorings());
		 extractRenamePatterns(classDiff, map);
      }
      for(UMLClassMoveDiff classDiff : classMoveDiffList) {
         refactorings.addAll(classDiff.getRefactorings());
		 extractRenamePatterns(classDiff, map);
      }
      for(UMLClassMoveDiff classDiff : innerClassMoveDiffList) {
         refactorings.addAll(classDiff.getRefactorings());
		 extractRenamePatterns(classDiff, map);
      }
      for(UMLClassRenameDiff classDiff : classRenameDiffList) {
         refactorings.addAll(classDiff.getRefactorings());
		 extractRenamePatterns(classDiff, map);
      }
	  for(Replacement pattern : map.keySet()) {
		 UMLClassBaseDiff diff = getUMLClassDiffWithAttribute(pattern);
		 Set<CandidateAttributeRefactoring> set = map.get(pattern);
		 for(CandidateAttributeRefactoring candidate : set) {
			 if(candidate.getOriginalVariableDeclaration() == null && candidate.getRenamedVariableDeclaration() == null) {
				 if(diff != null) {
					 UMLAttribute a1 = diff.findAttributeInOriginalClass(pattern.getBefore());
					 UMLAttribute a2 = diff.findAttributeInNextClass(pattern.getAfter());
					 if(!diff.getOriginalClass().containsAttributeWithName(pattern.getAfter()) &&
								!diff.getNextClass().containsAttributeWithName(pattern.getBefore())) {
						 RenameAttributeRefactoring ref = new RenameAttributeRefactoring(a1.getVariableDeclaration(), a2.getVariableDeclaration(),
								 diff.getOriginalClassName(), diff.getNextClassName(), set);
						 if(!refactorings.contains(ref)) {
							 refactorings.add(ref);
							 break;//it's not necessary to repeat the same process for all candidates in the set
						 }
					 }
				 }
			 }
			 else if(candidate.getOriginalVariableDeclaration() != null) {
				 UMLClassBaseDiff diff1 = getUMLClassDiffWithExistingAttributeAfter(pattern);
				 UMLClassBaseDiff diff2 = getUMLClassDiffWithNewAttributeAfter(pattern);
				 if(diff1 != null) {
					 UMLAttribute a2 = diff1.findAttributeInNextClass(pattern.getAfter());
					 if(candidate.getOriginalVariableDeclaration().isAttribute()) {
						 UMLClassBaseDiff originalClassDiff = getUMLClassDiff(candidate.getOriginalAttribute().getClassName());
						 if(originalClassDiff != null && originalClassDiff.removedAttributes.contains(candidate.getOriginalAttribute())) {
							 ReplaceAttributeRefactoring ref = new ReplaceAttributeRefactoring(candidate.getOriginalAttribute(), a2, set);
							 if(!refactorings.contains(ref)) {
								 refactorings.add(ref);
								 break;//it's not necessary to repeat the same process for all candidates in the set
							 }
						 }
					 }
				 }
				 else if(diff2 != null) {
					 UMLAttribute a2 = diff2.findAttributeInNextClass(pattern.getAfter());
					 if(candidate.getOriginalVariableDeclaration().isAttribute()) {
						 UMLClassBaseDiff originalClassDiff = getUMLClassDiff(candidate.getOriginalAttribute().getClassName());
						 if(originalClassDiff != null && originalClassDiff.removedAttributes.contains(candidate.getOriginalAttribute())) {
							 MoveAndRenameAttributeRefactoring ref = new MoveAndRenameAttributeRefactoring(candidate.getOriginalAttribute(), a2, set);
							 if(!refactorings.contains(ref)) {
								 refactorings.add(ref);
								 break;//it's not necessary to repeat the same process for all candidates in the set
							 }
						 }
					 }
				 }
			 }
		 }
	  }
	  refactorings.addAll(identifyExtractClassRefactorings(commonClassDiffList));
      refactorings.addAll(identifyExtractClassRefactorings(classMoveDiffList));
      refactorings.addAll(identifyExtractClassRefactorings(innerClassMoveDiffList));
      refactorings.addAll(identifyExtractClassRefactorings(classRenameDiffList));
      checkForOperationMovesBetweenCommonClasses();
      checkForExtractedAndMovedOperations(getOperationBodyMappersInCommonClasses(), getAddedAndExtractedOperationsInCommonClasses());
      checkForExtractedAndMovedOperations(getOperationBodyMappersInMovedAndRenamedClasses(), getAddedOperationsInMovedAndRenamedClasses());
      checkForOperationMovesIncludingAddedClasses();
      checkForOperationMovesIncludingRemovedClasses();
      refactorings.addAll(checkForAttributeMovesBetweenCommonClasses());
      refactorings.addAll(checkForAttributeMovesIncludingAddedClasses());
      refactorings.addAll(checkForAttributeMovesIncludingRemovedClasses());
      refactorings.addAll(this.refactorings);
      return new ArrayList<Refactoring>(refactorings);
   }

   private void extractRenamePatterns(UMLClassBaseDiff classDiff, Map<Replacement, Set<CandidateAttributeRefactoring>> map) {
	  for(CandidateAttributeRefactoring candidate : classDiff.getCandidateAttributeRenames()) {
		 String before = PrefixSuffixUtils.normalize(candidate.getOriginalVariableName());
		 String after = PrefixSuffixUtils.normalize(candidate.getRenamedVariableName());
		 Replacement renamePattern = new Replacement(before, after, ReplacementType.VARIABLE_NAME);
		 if(map.containsKey(renamePattern)) {
			 map.get(renamePattern).add(candidate);
		 }
		 else {
			 Set<CandidateAttributeRefactoring> set = new LinkedHashSet<CandidateAttributeRefactoring>();
			 set.add(candidate);
			 map.put(renamePattern, set);
		 }
	  }
   }

   private void checkForExtractedAndMovedOperations(List<UMLOperationBodyMapper> mappers, List<UMLOperation> addedOperations) {
      for(Iterator<UMLOperation> addedOperationIterator = addedOperations.iterator(); addedOperationIterator.hasNext();) {
    	  UMLOperation addedOperation = addedOperationIterator.next();
    	  for(UMLOperationBodyMapper mapper : mappers) {
    		  if((mapper.nonMappedElementsT1() > 0 || !mapper.getReplacementsInvolvingMethodInvocation().isEmpty()) && !mapper.containsExtractOperationRefactoring(addedOperation)) {
               Set<OperationInvocation> operationInvocations = mapper.getOperation2().getAllOperationInvocations();
               OperationInvocation addedOperationInvocation = null;
               for(OperationInvocation invocation : operationInvocations) {
                  if(invocation.matchesOperation(addedOperation, mapper.getOperation2().variableTypeMap(), this)) {
                     addedOperationInvocation = invocation;
                     break;
                  }
               }
               if(addedOperationInvocation != null) {
            	  List<String> arguments = addedOperationInvocation.getArguments();
            	  List<String> parameters = addedOperation.getParameterNameList();
            	  Map<String, String> parameterToArgumentMap2 = new LinkedHashMap<String, String>();
            	  //special handling for methods with varargs parameter for which no argument is passed in the matching invocation
				  int size = Math.min(arguments.size(), parameters.size());
            	  for(int i=0; i<size; i++) {
            		  parameterToArgumentMap2.put(parameters.get(i), arguments.get(i));
            	  }
            	  String className = mapper.getOperation2().getClassName();
            	  List<UMLAttribute> attributes = new ArrayList<UMLAttribute>();
            	  if(className.contains(".") && isNumeric(className.substring(className.lastIndexOf(".")+1, className.length()))) {
            		  //add enclosing class fields + anonymous class fields
            		  UMLClassBaseDiff umlClassDiff = getUMLClassDiff(className.substring(0, className.lastIndexOf(".")));
            		  attributes.addAll(umlClassDiff.originalClassAttributesOfType(addedOperation.getClassName()));
            		  for(UMLAnonymousClass anonymous : umlClassDiff.getOriginalClass().getAnonymousClassList()) {
            			  if(anonymous.getName().equals(className)) {
            				  attributes.addAll(anonymous.attributesOfType(addedOperation.getClassName()));
            				  break;
            			  }
            		  }
            	  }
            	  else {
            		  UMLClassBaseDiff umlClassDiff = getUMLClassDiff(className);
            		  attributes.addAll(umlClassDiff.originalClassAttributesOfType(addedOperation.getClassName()));
            	  }
            	  Map<String, String> parameterToArgumentMap1 = new LinkedHashMap<String, String>();
            	  for(UMLAttribute attribute : attributes) {
            		  parameterToArgumentMap1.put(attribute.getName() + ".", "");
            		  parameterToArgumentMap2.put("this.", "");
            	  }
            	  if(addedOperationInvocation.getExpression() != null) {
            		  parameterToArgumentMap1.put(addedOperationInvocation.getExpression() + ".", "");
            		  parameterToArgumentMap2.put("this.", "");
            	  }
                  UMLOperationBodyMapper operationBodyMapper = new UMLOperationBodyMapper(mapper, addedOperation, parameterToArgumentMap1, parameterToArgumentMap2);
                  if(!anotherAddedMethodExistsWithBetterMatchingInvocationExpression(addedOperationInvocation, addedOperation, addedOperations) &&
                		  !conflictingExpression(addedOperationInvocation, addedOperation, mapper.getOperation2().variableTypeMap()) &&
                		  extractAndMoveMatchCondition(operationBodyMapper, mapper)) {
                	  if(className.equals(addedOperation.getClassName())) {
                		  //extract inside moved or renamed class
                		  ExtractOperationRefactoring extractOperationRefactoring =
   	                           new ExtractOperationRefactoring(operationBodyMapper, mapper.getOperation2(), addedOperationInvocation);
   	                      refactorings.add(extractOperationRefactoring);
   	                      deleteAddedOperation(addedOperation);
                	  }
                	  else if(isSubclassOf(className, addedOperation.getClassName())) {
                		  //extract and pull up method
                		  ExtractAndMoveOperationRefactoring extractOperationRefactoring =
   	                           new ExtractAndMoveOperationRefactoring(operationBodyMapper, mapper.getOperation2());
   	                      refactorings.add(extractOperationRefactoring);
   	                      deleteAddedOperation(addedOperation);
                	  }
                	  else if(isSubclassOf(addedOperation.getClassName(), className)) {
                		  //extract and push down method
                		  ExtractAndMoveOperationRefactoring extractOperationRefactoring =
   	                           new ExtractAndMoveOperationRefactoring(operationBodyMapper, mapper.getOperation2());
   	                      refactorings.add(extractOperationRefactoring);
   	                      deleteAddedOperation(addedOperation);
                	  }
                	  else if(addedOperation.getClassName().startsWith(className + ".")) {
                		  //extract and move to inner class
                		  ExtractAndMoveOperationRefactoring extractOperationRefactoring =
      	                       new ExtractAndMoveOperationRefactoring(operationBodyMapper, mapper.getOperation2());
      	                  refactorings.add(extractOperationRefactoring);
      	                  deleteAddedOperation(addedOperation);
                	  }
                	  else if(className.startsWith(addedOperation.getClassName() + ".")) {
                		  //extract and move to outer class
                		  ExtractAndMoveOperationRefactoring extractOperationRefactoring =
      	                       new ExtractAndMoveOperationRefactoring(operationBodyMapper, mapper.getOperation2());
      	                  refactorings.add(extractOperationRefactoring);
      	                  deleteAddedOperation(addedOperation);
                	  }
                	  else if(sourceClassImportsTargetClassAfterRefactoring(className, addedOperation.getClassName()) ||
                			  sourceClassImportsSuperclassOfTargetClassAfterRefactoring(className, addedOperation.getClassName())) {
                		  //extract and move
	                      ExtractAndMoveOperationRefactoring extractOperationRefactoring =
	                           new ExtractAndMoveOperationRefactoring(operationBodyMapper, mapper.getOperation2());
	                      refactorings.add(extractOperationRefactoring);
	                      deleteAddedOperation(addedOperation);
                	  }
                  }
               }
            }
         }
      }
   }

   private boolean conflictingExpression(OperationInvocation invocation, UMLOperation addedOperation, Map<String, UMLType> variableTypeMap) {
	   String expression = invocation.getExpression();
	   if(expression != null && variableTypeMap.containsKey(expression)) {
		   UMLType type = variableTypeMap.get(expression);
		   UMLClassBaseDiff classDiff = getUMLClassDiff(addedOperation.getClassName());
		   boolean superclassRelationship = false;
		   if(classDiff != null && classDiff.getNewSuperclass() != null &&
				   classDiff.getNewSuperclass().equals(type)) {
			   superclassRelationship = true;
		   }
		   if(!addedOperation.getNonQualifiedClassName().equals(type.getClassType()) && !superclassRelationship) {
			   return true;
		   }
	   }
	   return false;
   }

   private boolean anotherAddedMethodExistsWithBetterMatchingInvocationExpression(OperationInvocation invocation, UMLOperation addedOperation, List<UMLOperation> addedOperations) {
	   String expression = invocation.getExpression();
	   if(expression != null) {
		   int originalDistance = StringDistance.editDistance(expression, addedOperation.getNonQualifiedClassName());
		   for(UMLOperation operation : addedOperations) {
			   UMLClassBaseDiff classDiff = getUMLClassDiff(operation.getClassName());
			   boolean isInterface = classDiff != null ? classDiff.nextClass.isInterface() : false;
			   if(!operation.equals(addedOperation) && addedOperation.equalSignature(operation) && !operation.isAbstract() && !isInterface) {
				   int newDistance = StringDistance.editDistance(expression, operation.getNonQualifiedClassName());
				   if(newDistance < originalDistance) {
					   return true;
				   }
			   }
		   }
	   }
	   return false;
   }

   private boolean extractAndMoveMatchCondition(UMLOperationBodyMapper operationBodyMapper, UMLOperationBodyMapper parentMapper) {
	   List<AbstractCodeMapping> mappingList = operationBodyMapper.getMappings();
	   if(operationBodyMapper.getOperation2().isGetter() && mappingList.size() == 1) {
		   List<AbstractCodeMapping> parentMappingList = parentMapper.getMappings();
		   for(AbstractCodeMapping mapping : parentMappingList) {
			   if(mapping.getFragment1().equals(mappingList.get(0).getFragment1())) {
				   return false;
			   }
		   }
	   }
	   int mappings = operationBodyMapper.mappingsWithoutBlocks();
	   int nonMappedElementsT1 = operationBodyMapper.nonMappedElementsT1();
	   int nonMappedElementsT2 = operationBodyMapper.nonMappedElementsT2();
	   List<AbstractCodeMapping> exactMatchList = operationBodyMapper.getExactMatches();
	   int exactMatches = exactMatchList.size();
	   return mappings > 0 && (mappings > nonMappedElementsT2 ||
			   (exactMatches == mappings && nonMappedElementsT1 == 0) ||
			   (exactMatches == 1 && !exactMatchList.get(0).getFragment1().throwsNewException() && nonMappedElementsT2-exactMatches < 10) ||
			   (exactMatches > 1 && nonMappedElementsT2-exactMatches < 20) ||
			   (mappings == 1 && mappings > operationBodyMapper.nonMappedLeafElementsT2()));
   }

   private void checkForOperationMovesIncludingRemovedClasses() {
      List<UMLOperation> addedOperations = getAddedAndExtractedOperationsInCommonClasses();
      /*for(UMLClass addedClass : addedClasses) {
    	  addedOperations.addAll(addedClass.getOperations());
      }*/
      List<UMLOperation> removedOperations = getRemovedOperationsInCommonClasses();
      for(UMLClass removedClass : removedClasses) {
    	  removedOperations.addAll(removedClass.getOperations());
      }
      if(removedOperations.size() <= MAXIMUM_NUMBER_OF_COMPARED_METHODS || addedOperations.size() <= MAXIMUM_NUMBER_OF_COMPARED_METHODS) {
    	  checkForOperationMoves(addedOperations, removedOperations);
      }
   }

   private void checkForOperationMovesIncludingAddedClasses() {
      List<UMLOperation> addedOperations = getAddedOperationsInCommonClasses();
      for(UMLClass addedClass : addedClasses) {
    	  addedOperations.addAll(addedClass.getOperations());
      }
      List<UMLOperation> removedOperations = getRemovedOperationsInCommonClasses();
      /*for(UMLClass removedClass : removedClasses) {
    	  removedOperations.addAll(removedClass.getOperations());
      }*/
      if(removedOperations.size() <= MAXIMUM_NUMBER_OF_COMPARED_METHODS || addedOperations.size() <= MAXIMUM_NUMBER_OF_COMPARED_METHODS) {
    	  checkForOperationMoves(addedOperations, removedOperations);
      }
   }

   private void checkForOperationMovesBetweenCommonClasses() {
      List<UMLOperation> addedOperations = getAddedAndExtractedOperationsInCommonClasses();
      List<UMLOperation> removedOperations = getRemovedOperationsInCommonClasses();
      if(removedOperations.size() <= MAXIMUM_NUMBER_OF_COMPARED_METHODS || addedOperations.size() <= MAXIMUM_NUMBER_OF_COMPARED_METHODS) {
    	  checkForOperationMoves(addedOperations, removedOperations);
      }
   }

   private void checkForOperationMovesBetweenRemovedAndAddedClasses() {
	   Set<UMLType> interfacesImplementedByAddedClasses = new LinkedHashSet<UMLType>();
	   for(UMLClass addedClass : addedClasses) {
		   interfacesImplementedByAddedClasses.addAll(addedClass.getImplementedInterfaces());
	   }
	   Set<UMLType> interfacesImplementedByRemovedClasses = new LinkedHashSet<UMLType>();
	   for(UMLClass removedClass : removedClasses) {
		   interfacesImplementedByRemovedClasses.addAll(removedClass.getImplementedInterfaces());
	   }
	   Set<UMLType> interfaceIntersection = new LinkedHashSet<UMLType>(interfacesImplementedByAddedClasses);
	   interfaceIntersection.retainAll(interfacesImplementedByRemovedClasses);
	   List<UMLOperation> addedOperations = new ArrayList<UMLOperation>();
	   for(UMLClass addedClass : addedClasses) {
		   if(!addedClass.implementsInterface(interfaceIntersection) && !outerClassMovedOrRenamed(addedClass)) {
			   addedOperations.addAll(addedClass.getOperations());
		   }
	   }
	   List<UMLOperation> removedOperations = new ArrayList<UMLOperation>();
	   for(UMLClass removedClass : removedClasses) {
		   if(!removedClass.implementsInterface(interfaceIntersection) && !outerClassMovedOrRenamed(removedClass)) {
			   removedOperations.addAll(removedClass.getOperations());
		   }
	   }
	   if(removedOperations.size() <= MAXIMUM_NUMBER_OF_COMPARED_METHODS || addedOperations.size() <= MAXIMUM_NUMBER_OF_COMPARED_METHODS) {
		   checkForOperationMoves(addedOperations, removedOperations);
	   }
   }

   private boolean outerClassMovedOrRenamed(UMLClass umlClass) {
	   if(!umlClass.isTopLevel()) {
		   for(UMLClassMoveDiff diff : classMoveDiffList) {
			   if(diff.getOriginalClass().getName().equals(umlClass.getPackageName()) ||
					   diff.getMovedClass().getName().equals(umlClass.getPackageName())) {
				   return true;
			   }
		   }
		   for(UMLClassRenameDiff diff : classRenameDiffList) {
			   if(diff.getOriginalClass().getName().equals(umlClass.getPackageName()) ||
					   diff.getRenamedClass().getName().equals(umlClass.getPackageName())) {
				   return true;
			   }
		   }
	   }
	   return false;
   }

   private void checkForOperationMoves(List<UMLOperation> addedOperations, List<UMLOperation> removedOperations) {
	   if(addedOperations.size() <= removedOperations.size()) {
	      for(Iterator<UMLOperation> addedOperationIterator = addedOperations.iterator(); addedOperationIterator.hasNext();) {
	         UMLOperation addedOperation = addedOperationIterator.next();
	         TreeMap<Integer, List<UMLOperationBodyMapper>> operationBodyMapperMap = new TreeMap<Integer, List<UMLOperationBodyMapper>>();
	         for(Iterator<UMLOperation> removedOperationIterator = removedOperations.iterator(); removedOperationIterator.hasNext();) {
	            UMLOperation removedOperation = removedOperationIterator.next();
	            
	            UMLOperationBodyMapper operationBodyMapper = new UMLOperationBodyMapper(removedOperation, addedOperation);
	            operationBodyMapper.getMappings();
	            int mappings = operationBodyMapper.mappingsWithoutBlocks();
	            if(mappings > 0 && mappedElementsMoreThanNonMappedT1AndT2(mappings, operationBodyMapper)) {
	               int exactMatches = operationBodyMapper.exactMatches();
	               if(operationBodyMapperMap.containsKey(exactMatches)) {
	                  List<UMLOperationBodyMapper> mapperList = operationBodyMapperMap.get(exactMatches);
	                  mapperList.add(operationBodyMapper);
	               }
	               else {
	                  List<UMLOperationBodyMapper> mapperList = new ArrayList<UMLOperationBodyMapper>();
	                  mapperList.add(operationBodyMapper);
	                  operationBodyMapperMap.put(exactMatches, mapperList);
	               }
	            }
	         }
	         if(!operationBodyMapperMap.isEmpty()) {
	            List<UMLOperationBodyMapper> firstMappers = firstMappers(operationBodyMapperMap);
	            addedOperationIterator.remove();
	            boolean sameSourceAndTargetClass = sameSourceAndTargetClass(firstMappers);
	            if(sameSourceAndTargetClass) {
	            	TreeSet<UMLOperationBodyMapper> set = new TreeSet<UMLOperationBodyMapper>();
	            	set.addAll(firstMappers);
	            	UMLOperationBodyMapper bestMapper = set.first();
	            	firstMappers.clear();
	            	firstMappers.add(bestMapper);
	            }
	            for(UMLOperationBodyMapper firstMapper : firstMappers) {
	               UMLOperation removedOperation = firstMapper.getOperation1();
	               if(sameSourceAndTargetClass) {
	                  removedOperations.remove(removedOperation);
	               }
	
	               Refactoring refactoring = null;
	               if(removedOperation.getClassName().equals(addedOperation.getClassName())) {
	            	  if (addedOperation.equalParameters(removedOperation)) {
	            		  //refactoring = new RenameOperationRefactoring(removedOperation, addedOperation);
	            	  } else {
	            		  // Methods in the same class with similar body but different signature
	            	  }
	               }
	               else if(removedOperation.isConstructor() == addedOperation.isConstructor() &&
	            		   isSubclassOf(removedOperation.getClassName(), addedOperation.getClassName()) && addedOperation.compatibleSignature(removedOperation)) {
	                  refactoring = new PullUpOperationRefactoring(firstMapper);
	               }
	               else if(removedOperation.isConstructor() == addedOperation.isConstructor() &&
	            		   isSubclassOf(addedOperation.getClassName(), removedOperation.getClassName()) && addedOperation.compatibleSignature(removedOperation)) {
	                  refactoring = new PushDownOperationRefactoring(firstMapper);
	               }
	               else if(removedOperation.isConstructor() == addedOperation.isConstructor() &&
	            		   movedMethodSignature(removedOperation, addedOperation) && !refactoringListContainsAnotherMoveRefactoringWithTheSameOperations(removedOperation, addedOperation)) {
	                  refactoring = new MoveOperationRefactoring(firstMapper);
	               }
	               if(refactoring != null) {
	                  deleteRemovedOperation(removedOperation);
	                  deleteAddedOperation(addedOperation);
	                  refactorings.add(refactoring);
	                  UMLClass addedClass = getAddedClass(addedOperation.getClassName());
	                  if(addedClass != null) {
	                	  checkForExtractedOperationsWithinMovedMethod(firstMapper, addedClass);
	                  }
	               }
	            }
	         }
	      }
      }
      else {
    	  for(Iterator<UMLOperation> removedOperationIterator = removedOperations.iterator(); removedOperationIterator.hasNext();) {
	         UMLOperation removedOperation = removedOperationIterator.next();
	         TreeMap<Integer, List<UMLOperationBodyMapper>> operationBodyMapperMap = new TreeMap<Integer, List<UMLOperationBodyMapper>>();
	         for(Iterator<UMLOperation> addedOperationIterator = addedOperations.iterator(); addedOperationIterator.hasNext();) {
	            UMLOperation addedOperation = addedOperationIterator.next();
	            
	            UMLOperationBodyMapper operationBodyMapper = new UMLOperationBodyMapper(removedOperation, addedOperation);
	            operationBodyMapper.getMappings();
	            int mappings = operationBodyMapper.mappingsWithoutBlocks();
	            if(mappings > 0 && mappedElementsMoreThanNonMappedT1AndT2(mappings, operationBodyMapper)) {
	               int exactMatches = operationBodyMapper.exactMatches();
	               if(operationBodyMapperMap.containsKey(exactMatches)) {
	                  List<UMLOperationBodyMapper> mapperList = operationBodyMapperMap.get(exactMatches);
	                  mapperList.add(operationBodyMapper);
	               }
	               else {
	                  List<UMLOperationBodyMapper> mapperList = new ArrayList<UMLOperationBodyMapper>();
	                  mapperList.add(operationBodyMapper);
	                  operationBodyMapperMap.put(exactMatches, mapperList);
	               }
	            }
	         }
	         if(!operationBodyMapperMap.isEmpty()) {
	            List<UMLOperationBodyMapper> firstMappers = firstMappers(operationBodyMapperMap);
	            removedOperationIterator.remove();
	            boolean sameSourceAndTargetClass = sameSourceAndTargetClass(firstMappers);
	            if(sameSourceAndTargetClass) {
	            	TreeSet<UMLOperationBodyMapper> set = new TreeSet<UMLOperationBodyMapper>();
	            	set.addAll(firstMappers);
	            	UMLOperationBodyMapper bestMapper = set.first();
	            	firstMappers.clear();
	            	firstMappers.add(bestMapper);
	            }
	            for(UMLOperationBodyMapper firstMapper : firstMappers) {
	               UMLOperation addedOperation = firstMapper.getOperation2();
	               if(sameSourceAndTargetClass) {
	                  addedOperations.remove(addedOperation);
	               }

	               Refactoring refactoring = null;
	               if(removedOperation.getClassName().equals(addedOperation.getClassName())) {
	            	  if (addedOperation.equalParameters(removedOperation)) {
	            		  //refactoring = new RenameOperationRefactoring(removedOperation, addedOperation);
	            	  } else {
	            		  // Methods in the same class with similar body but different signature
	            	  }
	               }
	               else if(removedOperation.isConstructor() == addedOperation.isConstructor() &&
	            		   isSubclassOf(removedOperation.getClassName(), addedOperation.getClassName()) && addedOperation.compatibleSignature(removedOperation)) {
	                  refactoring = new PullUpOperationRefactoring(firstMapper);
	               }
	               else if(removedOperation.isConstructor() == addedOperation.isConstructor() &&
	            		   isSubclassOf(addedOperation.getClassName(), removedOperation.getClassName()) && addedOperation.compatibleSignature(removedOperation)) {
	                  refactoring = new PushDownOperationRefactoring(firstMapper);
	               }
	               else if(removedOperation.isConstructor() == addedOperation.isConstructor() &&
	            		   movedMethodSignature(removedOperation, addedOperation) && !refactoringListContainsAnotherMoveRefactoringWithTheSameOperations(removedOperation, addedOperation)) {
	                  refactoring = new MoveOperationRefactoring(firstMapper);
	               }
	               if(refactoring != null) {
	                  deleteRemovedOperation(removedOperation);
	                  deleteAddedOperation(addedOperation);
	                  refactorings.add(refactoring);
	               }
	            }
	         }
	      }
      }
   }

	private List<UMLOperationBodyMapper> firstMappers(TreeMap<Integer, List<UMLOperationBodyMapper>> operationBodyMapperMap) {
		List<UMLOperationBodyMapper> firstMappers = new ArrayList<UMLOperationBodyMapper>(operationBodyMapperMap.get(operationBodyMapperMap.lastKey()));
		List<UMLOperationBodyMapper> extraMappers = operationBodyMapperMap.get(0);
		if(extraMappers != null && operationBodyMapperMap.lastKey() != 0) {
			for(UMLOperationBodyMapper extraMapper : extraMappers) {
				UMLOperation operation1 = extraMapper.getOperation1();
				UMLOperation operation2 = extraMapper.getOperation2();
				if(operation1.equalSignature(operation2)) {
					List<AbstractCodeMapping> mappings = extraMapper.getMappings();
					if(mappings.size() == 1) {
						Set<Replacement> replacements = mappings.get(0).getReplacements();
						if(replacements.size() == 1) {
							Replacement replacement = replacements.iterator().next();
							List<String> parameterNames1 = operation1.getParameterNameList();
							List<String> parameterNames2 = operation2.getParameterNameList();
							for(int i=0; i<parameterNames1.size(); i++) {
								String parameterName1 = parameterNames1.get(i);
								String parameterName2 = parameterNames2.get(i);
								if(replacement.getBefore().equals(parameterName1) &&
										replacement.getAfter().equals(parameterName2)) {
									firstMappers.add(extraMapper);
									break;
								}
							}
						}
					}
				}
			}
		}
		return firstMappers;
	}

   private boolean sameSourceAndTargetClass(List<UMLOperationBodyMapper> mappers) {
	   if(mappers.size() == 1) {
		   return false;
	   }
	   String sourceClassName = null;
	   String targetClassName = null;
	   for (UMLOperationBodyMapper mapper : mappers) {
		   String mapperSourceClassName = mapper.getOperation1().getClassName();
		   if(sourceClassName == null) {
			   sourceClassName = mapperSourceClassName;
		   }
		   else if(!mapperSourceClassName.equals(sourceClassName)) {
			   return false;
		   }
		   String mapperTargetClassName = mapper.getOperation2().getClassName();
		   if(targetClassName == null) {
			   targetClassName = mapperTargetClassName;
		   }
		   else if(!mapperTargetClassName.equals(targetClassName)) {
			   return false;
		   }
	   }
	   return true;
   }

   private boolean mappedElementsMoreThanNonMappedT1AndT2(int mappings, UMLOperationBodyMapper operationBodyMapper) {
        int nonMappedElementsT1 = operationBodyMapper.nonMappedElementsT1();
		int nonMappedElementsT2 = operationBodyMapper.nonMappedElementsT2();
		int nonMappedStatementsDeclaringSameVariable = 0;
		for(StatementObject s1 : operationBodyMapper.getNonMappedLeavesT1()) {
			for(StatementObject s2 : operationBodyMapper.getNonMappedLeavesT2()) {
				if(s1.getVariableDeclarations().size() == 1 && s2.getVariableDeclarations().size() == 1) {
					VariableDeclaration v1 = s1.getVariableDeclarations().get(0);
					VariableDeclaration v2 = s2.getVariableDeclarations().get(0);
					if(v1.getVariableName().equals(v2.getVariableName()) && v1.getType().equals(v2.getType())) {
						nonMappedStatementsDeclaringSameVariable++;
					}
				}
			}
		}
		return (mappings > nonMappedElementsT1-nonMappedStatementsDeclaringSameVariable &&
				mappings > nonMappedElementsT2-nonMappedStatementsDeclaringSameVariable) ||
				(nonMappedElementsT1 == 0 && mappings > Math.floor(nonMappedElementsT2/2.0));
   }

   private boolean movedMethodSignature(UMLOperation removedOperation, UMLOperation addedOperation) {
	   if(addedOperation.getName().equals(removedOperation.getName()) &&
			   addedOperation.equalReturnParameter(removedOperation) &&
			   addedOperation.isAbstract() == removedOperation.isAbstract()) {
		   if(addedOperation.getParameters().equals(removedOperation.getParameters())) {
			   return true;
		   }
		   else {
			   // ignore parameters of types sourceClass and targetClass
			   List<UMLParameter> oldParameters = new ArrayList<UMLParameter>();
			   for (UMLParameter oldParameter : removedOperation.getParameters()) {
				   if (!oldParameter.getKind().equals("return")
						   && !looksLikeSameType(oldParameter.getType().getClassType(), addedOperation.getClassName())
						   && !looksLikeSameType(oldParameter.getType().getClassType(), removedOperation.getClassName())) {
					   oldParameters.add(oldParameter);
				   }
			   }
			   List<UMLParameter> newParameters = new ArrayList<UMLParameter>();
			   for (UMLParameter newParameter : addedOperation.getParameters()) {
				   if (!newParameter.getKind().equals("return") &&
						   !looksLikeSameType(newParameter.getType().getClassType(), addedOperation.getClassName()) &&
						   !looksLikeSameType(newParameter.getType().getClassType(), removedOperation.getClassName())) {
					   newParameters.add(newParameter);
				   }
			   }
			   return oldParameters.equals(newParameters) || oldParameters.containsAll(newParameters) || newParameters.containsAll(oldParameters);
		   }
	   }
	   return false;
   }

   private boolean refactoringListContainsAnotherMoveRefactoringWithTheSameOperations(UMLOperation removedOperation, UMLOperation addedOperation) {
	   for(Refactoring refactoring : refactorings) {
		   if(refactoring instanceof MoveOperationRefactoring) {
			   MoveOperationRefactoring moveRefactoring = (MoveOperationRefactoring)refactoring;
			   if(moveRefactoring.getOriginalOperation().equals(removedOperation)) {
				   return true;
			   }
		   }
	   }
	   return false;
   }

   private void deleteRemovedOperation(UMLOperation operation) {
      UMLClassBaseDiff classDiff = getUMLClassDiff(operation.getClassName());
      if(classDiff != null)
    	  classDiff.getRemovedOperations().remove(operation);
   }
   
   private void deleteAddedOperation(UMLOperation operation) {
      UMLClassBaseDiff classDiff = getUMLClassDiff(operation.getClassName());
      if(classDiff != null)
    	  classDiff.getAddedOperations().remove(operation);
   }

	private static boolean isNumeric(String str) {
		for(char c : str.toCharArray()) {
			if(!Character.isDigit(c)) return false;
		}
		return true;
	}
}
