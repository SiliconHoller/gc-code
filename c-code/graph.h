





typedef struct {

	int 	w;			//width of image
	int 	h;			//height of image
	int		numLabels;	//number of labels, default to two
	int		*labels;	//label array holding positions of labels
	int 	*verts;		//vertices array
	int		**neighbors;//neighbors array
	double	**capacity; //capacity array holding initial capacities for flow
	double 	**flow;		//flow array holding cumulative flows
} graph_t;

typedef struct {

	int u;//start of directed edge
	int v;//end of directed edge
	double available; //flow available along this edge
} edge_t;

typedef struct {

	edge_t *current; //current path segment
	edge_t *next; //next in path listing...default to NULL
	edge_t *previous;//previous in path listing...default to NULL

} pathsegment_t;


pathsegment_t * newPathSegment(void) {

	pathsegment_t ps = {NULL,NULL,NULL};
	return &ps;
}
