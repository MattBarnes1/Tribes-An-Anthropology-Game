/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package anthropologyapplication;

import anthropologyapplication.AutoMapper.AutoMapperGui;
import anthropologyapplication.AutoMapper.MapTile;
import anthropologyapplication.AutoMapper.Vector3;
import anthropologyapplication.internalLockers.internalBuildingLocker;
import anthropologyapplication.DisplayData.BuildingConstructionDisplayData;
import anthropologyapplication.Buildings.Workshop;
import anthropologyapplication.Buildings.Field;
import anthropologyapplication.Buildings.Granary;
import anthropologyapplication.Buildings.TribalHut;
import java.util.ArrayList;
import java.util.Iterator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Duke
 */
public class BuildingHandler {
                private final String NoFile = "NoFile";
                private internalBuildingLocker[] internalBuildingList = new internalBuildingLocker[] { 
                    new internalBuildingLocker(new TribalHut("Tribal Hut", "Goverment", new Time(0,0,0,0,0,0,5000), 0, 2,"TribalCamp.jpg", NoFile), true),
                    new internalBuildingLocker(new Granary("Granary", "Food Storage", new Time(0,0,0,0,0,0,5000), 0, 2, NoFile, NoFile), true),
                    new internalBuildingLocker(new Field ("Field", "A field", new Time(0,0,0,0,0,0,5000), 1, 4, NoFile, NoFile), true),
                    new internalBuildingLocker(new Workshop("Workship", "A workshop",  new Time(0,0,0,0,0,0,5000), 2, 2, NoFile, NoFile), true)                    
                };//this is a template list of all buildings
		private int BuildersBuilding = 0; //number of people building
		private ArrayList<BuildingConstructionDisplayData> BuildingsThatCanBeBuilt = new ArrayList<>(); //Used for building in menu
                 //This is updated for the player when they call so we don't have to iterate through a list.
                private int currentlyAllowedTier = 0; //Unlocked level
		private ArrayList<Building> BuildingsBeingConstructed = new ArrayList<>();
                private ArrayList<Building> BuildingsBuilt = new ArrayList<>();
                
                
		public BuildingHandler()
		{
                        updatePossibleBuildings();
		}

                private void updatePossibleBuildings()
                {
                    BuildingsThatCanBeBuilt.clear();
                    for(internalBuildingLocker aVal : internalBuildingList)
                    {
                        if(aVal.Available)
                        {
                            BuildingsThatCanBeBuilt.add(new BuildingConstructionDisplayData(aVal.myBuilding));
                        }
                    }
                    
                }
                
               
                
                
		public void lockBuilding(Class<? extends Building> aBuilding, TribalCampObject anObject)
                {
                    for(internalBuildingLocker A : internalBuildingList)
                    {
                        if(A.myBuilding.getClass() == aBuilding)
                        {
                            A.Available = false;
                            A.myBuilding.onLock(anObject);
                            
                        }
                    }
                }
                
                public void unlockBuilding(Class<? extends Building> aBuilding, TribalCampObject anObject)
                {
                    for(internalBuildingLocker A : internalBuildingList)
                    {
                        if(A.myBuilding.getClass() == aBuilding)
                        {
                            A.Available = true;
                            A.myBuilding.onUnlock(anObject);
                        }
                    }
                }
                
                public void update(GameTime T)
                {
                    Iterator<Building> buildingIterator = BuildingsBeingConstructed.iterator();
                    while(buildingIterator.hasNext())
                    {
                        Building myNext = buildingIterator.next();
                        if(internalBuildingList[myNext.getIndex()].Available)
                        {
                            myNext.update(T);
                            if(myNext.isFinishedBuilding())
                            {
                                BuildingsBuilt.add(myNext);
                                buildingIterator.remove();
                            }
                        }   
                    }
                }
                
		public void forceBuild(String aBuildingName, MapTile aLocation)
                {
			for(internalBuildingLocker A : internalBuildingList)
                        {
                            if(A.myBuilding.getBuildingName().compareTo(aBuildingName) == 0)
                            {
                                if(BuildingsBuilt.contains(A))
                                {
                                  if(A.myBuilding.canBuildMultiples())
                                  {
                                      Building newBuilding = A.myBuilding.Copy();
                                      newBuilding.forceBuildAtLocation(aLocation);
                                      BuildingsBuilt.add(newBuilding);
                                      AutoMapperGui.redrawMap();
                                      return;
                                  }
                                } else {
                                      Building newBuilding = A.myBuilding.Copy();
                                      newBuilding.forceBuildAtLocation(aLocation);
                                      BuildingsBuilt.add(newBuilding);
                                      AutoMapperGui.redrawMap();
                                      return;
                                }
                            }
                        }
                        throw new UnsupportedOperationException("Attempted to force build a building, but couldn't find it!");
                }

		public BuildingConstructionDisplayData startBuilding(String aBuildingName, MapTile aTile)
		{//TODO: Valid maptile check
                    BuildingConstructionDisplayData retVal = null;
			for(internalBuildingLocker A : internalBuildingList)
                        {
                            if(A.myBuilding.getBuildingName().compareTo(aBuildingName) == 0)
                            {
                                if(BuildingsBuilt.contains(A))
                                {
                                  if(A.myBuilding.canBuildMultiples())
                                  {
                                      Building newBuilding = A.myBuilding.Copy();
                                      newBuilding.startBuildingAtLocation(aTile);
                                      retVal = new BuildingConstructionDisplayData(newBuilding);
                                      BuildingsBeingConstructed.add(newBuilding);
                                  }
                                } else {
                                      Building newBuilding = A.myBuilding.Copy();
                                      newBuilding.startBuildingAtLocation(aTile);
                                      retVal = new BuildingConstructionDisplayData(newBuilding);
                                      BuildingsBeingConstructed.add(newBuilding);
                                }
                            }
                        }
                    return retVal;
		}

		public ArrayList<BuildingConstructionDisplayData> getBuiltBuildings()
		{
			ArrayList<BuildingConstructionDisplayData> myRetString = new ArrayList<BuildingConstructionDisplayData>();
                        for(Building B : BuildingsBuilt)
                        {
                            myRetString.add(new BuildingConstructionDisplayData(B));
                        }
                        return myRetString;
		}
                
                private int countBuildingsInList(ArrayList<Building> aBuildingList, Class<? extends Building> aClass)
                {
                    int Count = 0;
                    for(Building B : aBuildingList)
                    {
                        if(B.getClass() == aClass)
                        {
                            Count++;
                        }
                    }
                    return Count;
                }
                    
                
                
                public BuildingConstructionDisplayData[] updateBuildingsBeingConstructedDisplay()
                {
                    BuildingConstructionDisplayData[] Data = new BuildingConstructionDisplayData[BuildingsBeingConstructed.size()];
                    for(int i = 0 ; i < BuildingsBeingConstructed.size(); i++)
                    {
                        Data[i] = new BuildingConstructionDisplayData(BuildingsBeingConstructed.get(i));
                    }
                    return Data;
                }
                
                
		public ArrayList<BuildingConstructionDisplayData> getBuildable()
		{
			return BuildingsThatCanBeBuilt;
		}

		public void addBuilders(int amount)
		{
			BuildersBuilding += amount;
		}
                
                public boolean canRemoveMore()
                {
                    return ((BuildersBuilding - 1) > 0);
                }

		public void removeBuilders(int amount)
		{
			if(BuildersBuilding - amount < 0)
                        {
                            BuildersBuilding = 0;
                        } else {
                            BuildersBuilding -= amount;
                        }
		}

		public int getBuildersAmount()
		{
                    return BuildersBuilding;
		}

		public ArrayList<Building> getAllBuiltBuildingsByType(Class<? extends Building> theClass)
		{
                    ArrayList<Building> retBuilding = new ArrayList<Building>();
                    for(internalBuildingLocker A : internalBuildingList)
                    {
                        if(A.myBuilding.getClass() == theClass)
                        {
                           retBuilding.add(A.myBuilding);
                        }
                    }
                    return retBuilding;
		}


}
