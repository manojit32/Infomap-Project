#include<stdio.h>
#include<stdlib.h>
#include<time.h>
#define MAX_TREE_HT 10000

typedef struct Node
{
	int nodeNo;
	int degree;
	int *adjNodes;
	float *adjNodesWeight;
	int parent;
}GraphNode;

typedef struct block
{
    int n;
    int key;//key = freq
	struct block *left, *right;
}block;

int isHeap ;
int heapSize ;

block *tree;
char **codeList;
int *codeLength;

GraphNode **graph;
int V,E,K;
int ncdWalk;//num nodes covered during walk
char *nodeMark;//to mark whether nod has been already covered or not
int *count;
int counter;
int count1=0;
int *queue = NULL;
int front,rear;


void freeUpQ()
{
	if(queue)
		free(queue);
}

void initQ()
{
	if(queue == NULL)
		queue = calloc(V,sizeof(int));
	rear = -1;
	front = 0;
}

int isEmptyQ()
{
	if(rear == front-1)
		return 1;
	else return 0;
}

void enqueue(int n)
{
	queue[++rear] = n;
}

int dequeue()
{
	if(isEmptyQ())
	{
		initQ();
		printf("Underflow");
		return -99;
	}
	else
	{
		front++;
		return queue[front-1];
	}
}

void SkipToEndOfLine(FILE *fpInp)
{ while (getc(fpInp) != '\n');
}

void ReadInput_Edgelist_undirected()//creates graph from an edgelist for undirected graph #Added
{
	int i,x,y,w;
	FILE *fpInp;

	/*if ((fpInp = fopen("adj_with_community_rehashed_dolphins_lcc.txt", "r")) == NULL)
		{ fprintf(stderr, "Cannot open adj_with_community_rehashed_dolphins_lcc.txt\n"); exit(1); }*/

	if ((fpInp = fopen(argv[1], "r")) == NULL)
		{ fprintf(stderr, "graph2.inp\n"); exit(1); }

	fscanf(fpInp,"%d %d",&V,&E);//SkipToEndOfLine(fpInp);
	printf("got V and E\n");
	nodeMark = (char*)calloc(V,sizeof(char));
	graph = (GraphNode**)calloc(V,sizeof(GraphNode*));
	count = (int*)calloc(V,sizeof(int));
	tree = (block*)calloc(V,sizeof(block));
	codeList = (char**)calloc(V,sizeof(char*));
	codeLength = (int*)calloc(V,sizeof(int));
	for(i = 0;i<V;i++)
	{
		graph[i] = NULL;// (GraphNode*)malloc(sizeof(GraphNode));
		//graph[i]->nodeNo = i;
		//graph[i]->parent = -1;
		nodeMark[i] = 'w';
		//tree[i] = malloc(sizeof(block));
		tree[i].n = i;
		tree[i].left = NULL;
		tree[i].right = NULL;
		codeLength[i] = 0;
		//codeList[i] = (char*)calloc(1,sizeof(char));
	}
	for(i = 0;i<E;i++)
	{
		fscanf(fpInp,"%d %d",&x,&y);
		w = 1;
		if(graph[x] == NULL)
		{
			graph[x] = (GraphNode*)malloc(sizeof(GraphNode));
			graph[x]->nodeNo = x;
			graph[x]->degree = 0;
			graph[x]->adjNodes = (int *)calloc(1,sizeof(int));
			graph[x]->adjNodesWeight = (float *)calloc(1,sizeof(float));
			graph[x]->parent = -1;
		}
		graph[x]->degree += 1;
		graph[x]->adjNodes = realloc(graph[x]->adjNodes,graph[x]->degree*sizeof(int));
		graph[x]->adjNodesWeight = realloc(graph[x]->adjNodesWeight,graph[x]->degree*sizeof(float));
		graph[x]->adjNodes[graph[x]->degree - 1] = y;
		graph[x]->adjNodesWeight[graph[x]->degree - 1] = w;
		if(graph[y] == NULL)
		{
			graph[y] = (GraphNode*)malloc(sizeof(GraphNode));
			graph[y]->nodeNo = y;
			graph[y]->degree = 0;
			graph[y]->adjNodes = (int *)calloc(1,sizeof(int));
			graph[y]->adjNodesWeight = (float *)calloc(1,sizeof(float));
			graph[y]->parent = -1;
		}
		graph[y]->degree += 1;
		graph[y]->adjNodes = realloc(graph[y]->adjNodes,graph[y]->degree*sizeof(int));
		graph[y]->adjNodesWeight = realloc(graph[y]->adjNodesWeight,graph[y]->degree*sizeof(float));
		graph[y]->adjNodes[graph[y]->degree - 1] = x;
		graph[y]->adjNodesWeight[graph[y]->degree - 1] = w;

		SkipToEndOfLine(fpInp);
	}

	fclose(fpInp);

}

void printBlock(block *a)
{
    printf("&a = %u,a->n = %d, a->key = %d,a->left = %u,a->right = %u\n",a,a->n,a->key,a->left,a->right);
}


void startWalk(int S)
{
	srand(time(NULL));
	nodeMark[S] = 1;
	int u = S;
	printf("u = %d\n",u);
	int r,v;
	int i = 0;
	while(ncdWalk != V-1)
	{
		count[u] += 1;
		tree[u].key += 1;
		r = (int)(rand()%(graph[u]->degree));
		v = graph[u]->adjNodes[r];
		if(graph[u]->degree != 1 && v == graph[u]->parent)
		{
			r = (int)rand()%(graph[u]->degree);
			v = graph[u]->adjNodes[r];
		}
		graph[v]->parent = u;
		if(nodeMark[v] == 'w')
			ncdWalk++;
		nodeMark[v] = 'g';
		u = v;
		i++;
	}
	counter = i;

}

void printNodeCount()
{
	int i;
	printf("counter = %d\nNode Count\n",counter);
	for(i = 0;i < V;i++)
	{
		printf("%d %d\n",i,count[i]);
	}

}



// This constant can be avoided by explicitly calculating height of Huffman Tree

 
// A Huffman tree node
struct MinHeapNode
{
    int data;  // One of the input characters
    unsigned freq;  // Frequency of the character
    struct MinHeapNode *left, *right; // Left and right child of this node
};
 
// A Min Heap:  Collection of min heap (or Hufmman tree) nodes
struct MinHeap
{
    unsigned size;    // Current size of min heap
    unsigned capacity;   // capacity of min heap
    struct MinHeapNode **array;  // Attay of minheap node pointers
};
 
// A utility function allocate a new min heap node with given character
// and frequency of the character
struct MinHeapNode* newNode(int data, unsigned freq)
{
    struct MinHeapNode* temp =
          (struct MinHeapNode*) malloc(sizeof(struct MinHeapNode));
    temp->left = temp->right = NULL;
    temp->data = data;
    temp->freq = freq;
    return temp;
}
 
// A utility function to create a min heap of given capacity
struct MinHeap* createMinHeap(unsigned capacity)
{
    struct MinHeap* minHeap =
         (struct MinHeap*) malloc(sizeof(struct MinHeap));
    minHeap->size = 0;  // current size is 0
    minHeap->capacity = capacity;
    minHeap->array =
     (struct MinHeapNode**)malloc(minHeap->capacity * sizeof(struct MinHeapNode*));
    return minHeap;
}
 
// A utility function to swap two min heap nodes
void swapMinHeapNode(struct MinHeapNode** a, struct MinHeapNode** b)
{
    struct MinHeapNode* t = *a;
    *a = *b;
    *b = t;
}
 
// The standard minHeapify function.
void minHeapify(struct MinHeap* minHeap, int idx)
{
    int smallest = idx;
    int left = 2 * idx + 1;
    int right = 2 * idx + 2;
 
    if (left < minHeap->size &&
        minHeap->array[left]->freq < minHeap->array[smallest]->freq)
      smallest = left;
 
    if (right < minHeap->size &&
        minHeap->array[right]->freq < minHeap->array[smallest]->freq)
      smallest = right;
 
    if (smallest != idx)
    {
        swapMinHeapNode(&minHeap->array[smallest], &minHeap->array[idx]);
        minHeapify(minHeap, smallest);
    }
}
 
// A utility function to check if size of heap is 1 or not
int isSizeOne(struct MinHeap* minHeap)
{
    return (minHeap->size == 1);
}
 
// A standard function to extract minimum value node from heap
struct MinHeapNode* extractMin(struct MinHeap* minHeap)
{
    struct MinHeapNode* temp = minHeap->array[0];
    minHeap->array[0] = minHeap->array[minHeap->size - 1];
    --minHeap->size;
    minHeapify(minHeap, 0);
    return temp;
}
 
// A utility function to insert a new node to Min Heap
void insertMinHeap(struct MinHeap* minHeap, struct MinHeapNode* minHeapNode)
{
    ++minHeap->size;
    int i = minHeap->size - 1;
    while (i && minHeapNode->freq < minHeap->array[(i - 1)/2]->freq)
    {
        minHeap->array[i] = minHeap->array[(i - 1)/2];
        i = (i - 1)/2;
    }
    minHeap->array[i] = minHeapNode;
}
 
// A standard funvtion to build min heap
void buildMinHeap(struct MinHeap* minHeap)
{
    int n = minHeap->size - 1;
    int i;
    for (i = (n - 1) / 2; i >= 0; --i)
        minHeapify(minHeap, i);
}
 
// A utility function to print an array of size n
void printArr(int arr[], int n)
{
    int i;
    for (i = 0; i < n; ++i)
        printf("%d", arr[i]);
    printf(" length= %d",n);
    count1+=n;
    printf("\n");
}

 
// Utility function to check if this node is leaf
int isLeaf(struct MinHeapNode* root)
{
    return !(root->left) && !(root->right) ;
}
 
// Creates a min heap of capacity equal to size and inserts all character of 
// data[] in min heap. Initially size of min heap is equal to capacity
struct MinHeap* createAndBuildMinHeap(int data[], int freq[], int size)
{
    struct MinHeap* minHeap = createMinHeap(size);
	int i;
    for (i = 0; i < size; ++i)
        minHeap->array[i] = newNode(data[i], freq[i]);
    minHeap->size = size;
    buildMinHeap(minHeap);
    return minHeap;
}
 
// The main function that builds Huffman tree
struct MinHeapNode* buildHuffmanTree(int data[], int freq[], int size)
{
    struct MinHeapNode *left, *right, *top;
 
    // Step 1: Create a min heap of capacity equal to size.  Initially, there are
    // modes equal to size.
    struct MinHeap* minHeap = createAndBuildMinHeap(data, freq, size);
 
    // Iterate while size of heap doesn't become 1
    while (!isSizeOne(minHeap))
    {
        // Step 2: Extract the two minimum freq items from min heap
        left = extractMin(minHeap);
        right = extractMin(minHeap);
 
        // Step 3:  Create a new internal node with frequency equal to the
        // sum of the two nodes frequencies. Make the two extracted node as
        // left and right children of this new node. Add this node to the min heap
        // '$' is a special value for internal nodes, not used
        top = newNode('$', left->freq + right->freq);
        top->left = left;
        top->right = right;
        insertMinHeap(minHeap, top);
    }
 
    // Step 4: The remaining node is the root node and the tree is complete.
    return extractMin(minHeap);
}
 
// Prints huffman codes from the root of Huffman Tree.  It uses arr[] to
// store codes
void printCodes(struct MinHeapNode* root, int arr[], int top)
{
    // Assign 0 to left edge and recur
    if (root->left)
    {
        arr[top] = 0;
        printCodes(root->left, arr, top + 1);
    }
 
    // Assign 1 to right edge and recur
    if (root->right)
    {
        arr[top] = 1;
        printCodes(root->right, arr, top + 1);
    }
 
    // If this is a leaf node, then it contains one of the input
    // characters, print the character and its code from arr[]
    if (isLeaf(root))
    {
        printf("%d: ", root->data);
        printArr(arr, top);
    }
}
 
// The main function that builds a Huffman Tree and print codes by traversing
// the built Huffman Tree
void HuffmanCodes(int data[], int freq[], int size)
{
   //  Construct Huffman Tree
   struct MinHeapNode* root = buildHuffmanTree(data, freq, size);
 
   // Print Huffman codes using the Huffman tree built above
   int arr[MAX_TREE_HT], top = 0;
   printCodes(root, arr, top);
}
 
// Driver program to test above functions
/*int main()
{
    char arr[] = {'a', 'b', 'c', 'd', 'e', 'f'};
    int freq[] = {5, 9, 12, 13, 16, 45};
    int size = sizeof(arr)/sizeof(arr[0]);
    HuffmanCodes(arr, freq, size);
    return 0;
}
*/






int main(int argc,char argv[])
{
	ReadInput_Edgelist_undirected();
	printf("Graph reading done\n");
	int r = (int)(rand()%V);
	printf("Start = %d\n",r);
	startWalk(r);
	printf("ncdWalk = %d\n",ncdWalk);
	printNodeCount();
	printf("\n\n");
	int arr[V];
	int i;
	for(i=0;i<V;i++)
		arr[i]=i;
	HuffmanCodes(arr, count, V);
	printf("total codelength = %d",count1);
	double avgcount=(count1/V);
	printf("\n average count = %lf",avgcount);
	return 0;
}
